package com.ailk.jccard.mina;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.phw.core.lang.Collections;
import org.phw.ibatis.engine.PDao;
import org.phw.ibatis.util.PDaoEngines;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.jccard.mina.bean.AppItemBean;
import com.ailk.jccard.mina.bean.JCHeadBean;
import com.ailk.jccard.mina.bean.req.JCIF2ReqBodyBean;
import com.ailk.jccard.mina.bean.req.JCIF3ReqBodyBean;
import com.ailk.jccard.mina.bean.rsp.JCIF1Rsp01BodyBean;
import com.ailk.jccard.mina.bean.rsp.JCIF1Rsp02BodyBean;
import com.ailk.jccard.mina.bean.rsp.JCIF1Rsp04BodyBean;
import com.ailk.jccard.mina.bean.rsp.JCIF2RspBodyBean;
import com.ailk.jccard.mina.bean.rsp.JCIF3RspBodyBean;
import com.ailk.jccard.mina.iface.JCClientHandler;
import com.ailk.jccard.mina.utils.RequestUtils;
import com.ailk.jccard.mina.utils.ResponseUtils;
import com.ailk.mall.base.utils.StringUtils;
import com.ailk.phw.utils.ObjectUtils;
import com.alibaba.fastjson.JSON;

public class MinaClientHandler extends JCClientHandler {

    private static Logger logger = LoggerFactory.getLogger(MinaClientHandler.class);

    private final static String SQL_XML = "com/ailk/sql/jccard/JCClientSQL.xml";
    private static PDao dao = PDaoEngines.getDao(SQL_XML, "EcsStore");

    private List requestLs = Arrays.asList(
            ObjectUtils.populateBean(Collections.asMap("resultCode", (short) 0),
                    JCIF3RspBodyBean.class),
            ObjectUtils.populateBean(Collections.asMap("resultCode", (short) 0, "apdu", "IF2 Response Content."),
                    JCIF2RspBodyBean.class)
            );

    @Override
    public byte[] messageRequest(IoSession session, Object message) throws Exception {
        int jobType = getSessionInfo(session).getJobType();
        Map requestInfo = RequestUtils.parseServerRequestInfo(((IoBuffer) message).array(), jobType);
        logger.info("Request Info - " + JSON.toJSONString(requestInfo));

        String ifNo = StringUtils.toString(requestInfo.get("ifNo"));
        recordMessage((JCHeadBean) requestInfo.get("head"), requestInfo.get("body"), ifNo, jobType);
        if ("IF1".equals(ifNo)) {
            return null;
        }

        // TODO: 从ESS取得返回的数据
        Object head = ResponseUtils.generateJCHeadBean(ifNo, getSessionInfo(session));
        Object body = requestLs.get(getSessionOrderNo(session) - 1);
        logger.info("Response Head - " + JSON.toJSONString(head));
        logger.info("Response Body - " + JSON.toJSONString(body));
        return ResponseUtils.generateResponse(head, body);
    }

    public void recordMessage(JCHeadBean head, Object body, String ifNo, int jobType) {
        String id = StringUtils.toString(dao.selectMap("JCClientSQL.getSeq", new HashMap()).get("SEQ"));
        Map param = Collections.asMap("ID", id, "SESSIONID", head.getSessionId(), "IF_NO", ifNo);
        List<Map> appList = null;
        if (ifNo.equals("IF1")) {
            if (jobType == 1) {
                JCIF1Rsp01BodyBean jcBody = (JCIF1Rsp01BodyBean) body;
                param.put("RSP_RESULT", jcBody.getResultCode());
                param.put("CARD_PRODUCT_NAME", jcBody.getMerchantName());
                param.put("USERFLAG", jcBody.getUserFlag());
                appList = fetchAppList(id, jcBody.getApps());
            } else if (jobType == 2 || jobType == 3) {
                JCIF1Rsp02BodyBean jcBody = (JCIF1Rsp02BodyBean) body;
                param.put("RSP_RESULT", jcBody.getResultCode());
                appList = fetchAppList(id, jcBody.getApps());
            } else {
                param.put("RSP_RESULT", ((JCIF1Rsp04BodyBean) body).getResultCode());
            }
        } else if (ifNo.equals("IF2")) {
            param.put("RSPDATA", ((JCIF2ReqBodyBean) body).getApdu());
        } else if (ifNo.equals("IF3")) {
            param.put("RSPDATA", ((JCIF3ReqBodyBean) body).getData());
        }
        boolean transactionStart = false;
        try {
            transactionStart = dao.tryStart();
            // dao.startBatch();
            dao.insert("JCClientSQL.insertRspData", param);
            if (appList != null) {
                for (Map app : appList) {
                    dao.insert("JCClientSQL.insertRspSubData", app);
                }
            }
            // dao.executeBatch();
            dao.commit(transactionStart);
        } finally {
            dao.end(transactionStart);
        }
    }

    public List<Map> fetchAppList(String id, List<AppItemBean> appList) {
        List<Map> result = new ArrayList<Map>();
        for (AppItemBean app : appList) {
            result.add(Collections.asMap("ID", id, "NAME", app.getAppName(), "AID", app.getAppAid(),
                    "APPSIZE", app.getAppSize(), "MAYOPERATE", app.getAppOperateType(),
                    "SUPPLIERNAME", app.getProvider(), "FEEDESC", app.getFeeDesc()));
        }
        return result;
    }
}

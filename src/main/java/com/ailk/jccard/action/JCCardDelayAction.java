package com.ailk.jccard.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.phw.core.lang.Collections;
import org.phw.eop.support.EopAction;
import org.phw.eop.support.EopActionException;
import org.phw.ibatis.engine.PDao;
import org.phw.ibatis.util.PDaoEngines;

import com.ailk.jccard.mina.MinaClient;
import com.ailk.jccard.mina.MinaClientConfig;
import com.ailk.jccard.mina.bean.JCHeadBean;
import com.ailk.jccard.mina.bean.req.JCIF1ReqBodyBean;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * JAVA卡平台延迟类处理
 * 
 * @author maxim
 *
 */
public class JCCardDelayAction extends EopAction {

    private JCHeadBean headBean;

    private JCIF1ReqBodyBean if1ReqBean;

    private static final String CARD_OPERATE_OVER = "1";
    private static final String CARD_OPERATE_PROCESSING = "2";
    private Map cobRsq;

    @Override
    public Object doAction() throws EopActionException {
        JSONObject reqObject = JSON.parseObject((String) getParams().get("reqBean"));
        boolean firstCardSession = reqObject.getBoolean("firstCardSession");
        if (firstCardSession) { // 如果是第一次请求, 则需要启动mina
            createJCBean(reqObject);
            startMinaClient();
            cobRsq = new HashMap();
            cobRsq.put("hasMsg", "false");
            cobRsq.put("result", CARD_OPERATE_PROCESSING);
        } else {
            queryDataFromDB(reqObject);
        }

        return Collections.asMap("jcCardCobRsq", cobRsq);
    }

    private void createJCBean(JSONObject reqObject) {
        // 拼装headBean
        String sessionID = reqObject.getString("sessionID");
        String businessType = reqObject.getString("businessType");
        headBean = new JCHeadBean();
        headBean.setSessionId(sessionID);
        headBean.setTypeFlag(Byte.decode(businessType));
        // 拼装if1ReqBean
        if1ReqBean = new JCIF1ReqBodyBean();
        String operatorID = reqObject.getString("operatorID");
        if1ReqBean.setOperatorId(operatorID);
        if1ReqBean.setChannelCode(reqObject.getString("channel"));
        if1ReqBean.setChannelType(reqObject.getString("channelType"));
        if1ReqBean.setCity(reqObject.getString("city"));
        if1ReqBean.setDistrict(reqObject.getString("area"));
        if1ReqBean.setIccid(reqObject.getString("iccid"));
        if1ReqBean.setIdentityCode(reqObject.getString("idCard"));
        if1ReqBean.setImsi(reqObject.getString("imsi"));
        if1ReqBean.setJobType(Byte.decode(reqObject.getString("missionType")));
        if1ReqBean.setMsisdn(reqObject.getString("msisdn"));
        if1ReqBean.setOperateType(Byte.decode(reqObject.getString("operateType")));
        if1ReqBean.setProvince(reqObject.getString("province"));
        if1ReqBean.setRequestData(reqObject.getString("reqData"));
        List arrayList = new ArrayList();
        if1ReqBean.setProductIds(arrayList);
    }

    // 运行Mina
    private void startMinaClient() {
        // 启动一个线程启动mina
        new Thread() {
            @Override
            public void run() {
                MinaClientConfig.setServerAddress("127.0.0.1");
                MinaClientConfig.setServerPort(9123);
                MinaClient.startClient(headBean, if1ReqBean);
            }

        }.start();
    }

    private void queryDataFromDB(JSONObject jsonObject) {
        final String SQL_XML = "com/ailk/sql/jccard/JCCardSQL.xml";
        String dataSource = "EcsStore";
        PDao dao = PDaoEngines.getDao(SQL_XML, dataSource);

        String sessionID = jsonObject.getString("sessionID");
        List<HashMap> query = dao.select("JCCardSQL.queryRsqData", sessionID);

        cobRsq = new HashMap();
        if (Collections.isEmpty(query)) {
            cobRsq.put("hasMsg", false);
            cobRsq.put("result", CARD_OPERATE_PROCESSING);
            return;
        }

        cobRsq.put("hasMsg", true);
        cobRsq.put("result", CARD_OPERATE_PROCESSING); // 放入是否处理完成
        ArrayList<Map> if3Beans = new ArrayList<Map>();
        for (HashMap q : query) {
            String ifNo = MapUtils.getString(q, "IF_NO");
            if ("IF1".equals(ifNo)) {
                HashMap if1Bean = new HashMap();
                cobRsq.put("if1Bean", if1Bean);
                if1Bean.put("result", MapUtils.getString(q, "RSP_RESULT"));
                if1Bean.put("cardProductName", MapUtils.getString(q, "CARD_PRODUCT_NAME"));
                if1Bean.put("userFlag", MapUtils.getString(q, "USERFLAG"));
                List<HashMap> beanLst = dao.select("JCCardSQL.queryIF1RsqData", MapUtils.getString(q, "ID"));
                if1Bean.put("appNum", beanLst.size());
                if1Bean.put("beanLst", beanLst);
                cobRsq.put("result", CARD_OPERATE_OVER);
            } else {
                Map msg = new HashMap();
                msg.put("rspDate", MapUtils.getString(q, "RSPDATA"));
                if ("IF2".equals(ifNo)) {
                    cobRsq.put("if2Bean", msg);
                } else if ("IF4".equals(ifNo)) {
                    cobRsq.put("if4Bean", msg);
                } else {
                    if3Beans.add(msg);
                }
            }
        }
        if (!Collections.isEmpty(if3Beans)) {
            cobRsq.put("if3Beans", if3Beans);
        }

    }
}

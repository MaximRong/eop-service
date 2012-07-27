package com.ailk.thirdparty.action.num;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.phw.core.lang.Collections;
import org.phw.eop.api.bean.numqry.rsp.NumInfoBean;
import org.phw.eop.api.bean.numqry.rsp.NumQryRspBody;
import org.phw.eop.domain.EopSystemParam;
import org.phw.eop.support.EopAction;
import org.phw.eop.support.EopActionException;
import org.phw.eop.support.EopSystemParamAware;
import org.phw.ibatis.engine.PDao;
import org.phw.ibatis.util.PDaoEngines;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.base.KeyConst;
import com.ailk.base.NumConst;
import com.ailk.base.utils.FaceUtils;
import com.ailk.mall.number.QueryLocalNumService;
import com.ailk.thirdparty.utils.ReqProcessor;

public class QueryNumAction extends EopAction implements EopSystemParamAware {
    private static final Logger logger = LoggerFactory.getLogger(QueryNumAction.class);
    private static final String REQ_TEMPLATE = "com/ailk/face/thirdparty/num/QueryNum_Req.xml";
    private static final String RSP_TEMPLATE = "com/ailk/face/thirdparty/num/QueryNum_Rsp.xml";
    private static final String MALL_SQL = "com/ailk/sql/MallCommonSQL.xml";
    private PDao mallDao = PDaoEngines.getDao(MALL_SQL, "EcsStore");
    private EopSystemParam sysParam = null;

    @Override
    public Object doAction() throws EopActionException {
        Map inMap = new HashMap();
        try {
            // 校验请求报文
            inMap = ReqProcessor.checkReqXml(getStr(KeyConst.REQ_XML), REQ_TEMPLATE);
            // 号码权限控制
            numQueryAuth(inMap);
            // 执行查询
            Map retMap = new QueryLocalNumService().execute(inMap, "0");
            NumQryRspBody rsp = (NumQryRspBody) retMap.get("body");
            inMap.put(KeyConst.RESP_CODE, rsp.getRespCode());
            inMap.put(KeyConst.RESP_DESC, rsp.getRespDesc());
            // 处理查询结果
            List<Map> numInfo = new ArrayList<Map>();
            for (NumInfoBean numInfoBean : rsp.getNumInfo()) {
                Map numMap = new HashMap();
                numMap.put("NumID", numInfoBean.getNumID());
                numMap.put("NumPreFee", numInfoBean.getNumPreFee());
                numMap.put("NumLevel", numInfoBean.getNumLevel());
                numMap.put("NumMemo", numInfoBean.getNumMemo());
                numInfo.add(numMap);
            }

            inMap.put("NumInfo", numInfo);
            return FaceUtils.createRspXml(inMap, RSP_TEMPLATE);
        }
        catch (Throwable t) {
            inMap.put(KeyConst.RESP_CODE, NumConst.RESP_CODE_FAIL);
            inMap.put(KeyConst.RESP_DESC, t.getMessage());
            return FaceUtils.createRspXml(inMap, RSP_TEMPLATE);
        }
    }

    /**
     * 号码权限控制。
     * @param inMap
     */
    private void numQueryAuth(Map inMap) {
        String appId = sysParam.getEopApp().getAppid();
        Map sysMap = mallDao.selectMap("MallCommon.getAppSysCode", appId);
        if (Collections.isEmpty(sysMap)) {
            logger.error("未查询到第三方的SysCode信息{}.", appId);
            throw new RuntimeException("未查询到接入第三方的SysCode信息。");
        }
        inMap.put(KeyConst.SYSCODE, sysMap.get(KeyConst.SYSCODE));
        List authLst = mallDao.select("MallCommon.getNumQueryAuth", inMap);
        if (Collections.isEmpty(authLst)) {
            logger.warn("第三方无查询号码的权限{}", appId);
            throw new RuntimeException("第三方无查询号码的权限。");
        }
        String numTag = (String) ((Map) authLst.get(0)).get("NUM_TAG");
        if (StringUtils.isEmpty(numTag)) {
            logger.warn("第三方无号码标记限制{}", appId);
        }
        inMap.put(KeyConst.PREORDER_TAG, numTag);
        logger.info("第三方号码权限检查成功{}", appId);
    }

    @Override
    public void setSystemParam(EopSystemParam sysParam) {
        this.sysParam = sysParam;
    }

}

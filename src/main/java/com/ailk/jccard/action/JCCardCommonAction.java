package com.ailk.jccard.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.phw.eop.support.EopAction;
import org.phw.ibatis.engine.PDao;
import org.phw.ibatis.util.PDaoEngines;

import com.ailk.jccard.mina.MinaClient;
import com.ailk.jccard.mina.MinaClientConfig;
import com.ailk.jccard.mina.bean.JCHeadBean;
import com.ailk.jccard.mina.bean.req.JCIF1ReqBodyBean;
import com.alibaba.fastjson.JSONObject;

public abstract class JCCardCommonAction extends EopAction {

    protected JCHeadBean headBean;

    protected JCIF1ReqBodyBean if1ReqBean;

    protected Map cobRsq;

    protected void createJCBean(JSONObject reqObject) {
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

    protected void startMinaClient() {
        MinaClientConfig.setServerAddress("127.0.0.1");
        MinaClientConfig.setServerPort(9123);
        MinaClient.startClient(headBean, if1ReqBean);
    }

    protected PDao newDao() {
        final String SQL_XML = "com/ailk/sql/jccard/JCCardSQL.xml";
        String dataSource = "EcsStore";
        return PDaoEngines.getDao(SQL_XML, dataSource);
    }

    /**
     * 查询返回消息
     * @param jsonObject
     * @param dao
     * @return
     */
    protected List<HashMap> queryCallbackInfo(JSONObject jsonObject, PDao dao) {
        String sessionID = jsonObject.getString("sessionID");
        List<HashMap> query = dao.select("JCCardSQL.queryRsqData", sessionID);
        return query;
    }
}

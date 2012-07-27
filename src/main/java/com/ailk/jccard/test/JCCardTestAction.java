package com.ailk.jccard.test;

import java.util.HashMap;
import java.util.Map;

import org.phw.core.lang.Collections;
import org.phw.eop.support.EopAction;
import org.phw.eop.support.EopActionException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class JCCardTestAction extends EopAction {
    //    private JCHeadBean headBean;

    //    private JCIF1ReqBodyBean if1ReqBean;

    private Map cobRsq;

    @Override
    public Object doAction() throws EopActionException {
        JSONObject jsonObject = JSON.parseObject((String) getParams().get("reqBean"));
        //        createJCBean(jsonObject);
        // 启动mima
        excuteMina();

        cobRsq = new HashMap();
        cobRsq.put("hasMsg", false);
        HashMap msg = new HashMap();
        msg.put("rspDate", "aaaaa");
        cobRsq.put("if2Bean", msg);

        return Collections.asMap("jcCardCobRsq", cobRsq);
    }

    /*
    private void createJCBean(JSONObject jsonObject) {
        // 拼装headBean
        String sessionID = jsonObject.getString("sessionID");
        String businessType = jsonObject.getString("businessType");
        headBean = new JCHeadBean();
        headBean.setSessionId(sessionID);
        headBean.setTypeFlag(Byte.decode(businessType));
        // 拼装if1ReqBean
        if1ReqBean = new JCIF1ReqBodyBean();
        String operatorID = jsonObject.getString("operatorID");
        if1ReqBean.setOperatorId(operatorID);
        if1ReqBean.setChannelCode(jsonObject.getString("channel"));
        if1ReqBean.setChannelType(jsonObject.getString("channelType"));
        if1ReqBean.setCity(jsonObject.getString("city"));
        if1ReqBean.setDistrict(jsonObject.getString("area"));
        if1ReqBean.setIccid(jsonObject.getString("iccid"));
        if1ReqBean.setIdentityCode(jsonObject.getString("idCard"));
        if1ReqBean.setImsi(jsonObject.getString("imsi"));
        if1ReqBean.setJobType(Byte.decode(jsonObject.getString("missionType")));
        if1ReqBean.setMsisdn(jsonObject.getString("msisdn"));
        if1ReqBean.setOperateType(Byte.decode(jsonObject.getString("operateType")));
        if1ReqBean.setProvince(jsonObject.getString("province"));
        if1ReqBean.setRequestData(jsonObject.getString("reqData"));
        System.out.println(headBean);
        System.out.println(if1ReqBean);
    }
    */

    private void excuteMina() {
        // TODO Auto-generated method stub
    }

    public static void main(String[] args) {
        String str = "{\"area\":\"3400\",\"channel\":\"000000\",\"channelType\":\"01\",\"city\":\"340\",\"iccid\":\"iccid\",\"idCard\":\"320106198602041213\",\"imsi\":\"imsi\",\"missionType\":\"0x05\",\"msisdn\":\"123123\",\"operateType\":\"0x01\",\"productNum\":1,\"province\":\"34\",\"reqData\":\"aid\",\"sessionID\":\"12345678901234567890\"}";
        JSONObject parseObject = JSON.parseObject(str);
        System.out.println(parseObject);
        System.out.println(parseObject.get("area"));
    }
}

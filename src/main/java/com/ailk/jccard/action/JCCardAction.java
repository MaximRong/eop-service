package com.ailk.jccard.action;

import org.phw.eop.support.EopAction;
import org.phw.eop.support.EopActionException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class JCCardAction extends EopAction {

    // 卡片校验请求
    private static final String CARD_VALIDATE = "0x01";
    // 卡应用下载
    private static final String CARD_APP_DOWNLOAD = "0x05";

    @Override
    public Object doAction() throws EopActionException {
        EopAction instance = initInstance();
        return instance.doAction();
    }

    private EopAction initInstance() {
        EopAction instance = null;
        JSONObject reqObject = JSON.parseObject((String) getParams().get("reqBean"));
        String missionType = reqObject.getString("missionType");
        if (CARD_VALIDATE.equals(missionType)) {
            instance = new JCCardValidateAction();
        } else if (CARD_APP_DOWNLOAD.equals(missionType)) {
            instance = new JCCardAppDownLoadAction();
        }
        return instance;
    }
}

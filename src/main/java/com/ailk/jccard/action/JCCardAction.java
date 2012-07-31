package com.ailk.jccard.action;

import java.util.Arrays;
import java.util.List;

import org.phw.eop.support.EopAction;
import org.phw.eop.support.EopActionException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class JCCardAction extends EopAction {

    // 卡片校验请求
    //    private static final String CARD_VALIDATE = "0x01";
    //    // 卡应用下载
    //    private static final String CARD_APP_DOWNLOAD = "0x05";
    //    // 卡应用删除
    //    private static final String CARD_APP_DELETE = "0x06";

    private static final List INTIME_OPERATE_ID = Arrays.asList("0x01", "0x02", "0x03", "0x04");
    private static final List DELAY_OPERATE_ID = Arrays.asList("0x05", "0x06", "0x07", "0x08", "0x09");

    @Override
    public Object doAction() throws EopActionException {
        EopAction instance = initInstance();
        return instance.doAction(getParams());
    }

    private EopAction initInstance() {
        EopAction instance = null;
        JSONObject reqObject = JSON.parseObject((String) getParams().get("reqBean"));
        String missionType = reqObject.getString("missionType");
        if (INTIME_OPERATE_ID.contains(missionType)) {
            instance = new JCCardInTimeAction();
        } else if (DELAY_OPERATE_ID.contains(missionType)) {
            instance = new JCCardDelayAction();
        }
        return instance;
    }
}

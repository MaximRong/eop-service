package com.ailk.jccard.action;

import java.util.HashMap;
import java.util.List;

import org.phw.eop.support.EopActionException;
import org.phw.ibatis.engine.PDao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class JCCardValidateAction extends JCCardCommonAction {

    @Override
    public Object doAction() throws EopActionException {
        JSONObject reqObject = JSON.parseObject((String) getParams().get("reqBean"));
        createJCBean(reqObject);
        startMinaClient();
        queryDataFormDB(reqObject);
        return null;
    }

    private void queryDataFormDB(JSONObject reqObject) {
        PDao dao = newDao();
        List<HashMap> callbackInfo = queryCallbackInfo(reqObject, dao);
    }

}

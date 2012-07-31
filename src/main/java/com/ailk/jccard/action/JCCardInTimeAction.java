package com.ailk.jccard.action;

import java.util.HashMap;
import java.util.List;

import org.phw.ibatis.engine.PDao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * JAVA卡应用即使类处理
 * 0x01 卡片验证请求
 * 0x02 可下载应用请求列表
 * 0x03 已下载应用请求列表
 * 0x04 首页列表更新
 * 
 * @author maxim
 *
 */
public class JCCardInTimeAction extends JCCardCommonAction {

    private void queryDataFormDB(JSONObject reqObject) {
        PDao dao = newDao();
        List<HashMap> callbackInfo = queryCallbackInfo(reqObject, dao);
        processCallBackInfo(dao, callbackInfo);
    }

    @Override
    protected void subActionProcess() {
        JSONObject reqObject = JSON.parseObject((String) getParams().get("reqBean"));
        createJCBean(reqObject);
        startMinaClient();
        queryDataFormDB(reqObject);

    }

}

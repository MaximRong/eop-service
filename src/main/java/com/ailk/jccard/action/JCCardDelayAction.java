package com.ailk.jccard.action;

import java.util.HashMap;
import java.util.List;

import org.phw.core.lang.Collections;
import org.phw.ibatis.engine.PDao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * JAVA卡平台延迟类处理
 * 对应的接口有0x05 下载卡应用
 * 0x06 删除卡应用
 * 0x07 应用锁定请求
 * 0x08 应用解锁请求
 * 0x09 应用升级请求
 * 
 * @author maxim
 *
 */
public class JCCardDelayAction extends JCCardCommonAction {

    @Override
    protected void subActionProcess() {
        JSONObject reqObject = JSON.parseObject((String) getParams().get("reqBean"));
        boolean firstCardSession = reqObject.getBoolean("firstCardSession");
        if (firstCardSession) { // 如果是第一次请求, 则需要启动mina
            createJCBean(reqObject);
            startMinaClientInNewThread();
            cobRsq.put("hasMsg", "false");
            cobRsq.put("result", CARD_OPERATE_PROCESSING);
        } else {
            queryDataFromDB(reqObject);
        }
    }

    // 运行Mina
    private void startMinaClientInNewThread() {
        // 启动一个线程启动mina
        new Thread() {
            @Override
            public void run() {
                startMinaClient();
            }

        }.start();
    }

    private void queryDataFromDB(JSONObject jsonObject) {
        PDao dao = newDao();

        List<HashMap> query = queryCallbackInfo(jsonObject, dao);

        if (Collections.isEmpty(query)) {
            cobRsq.put("hasMsg", false);
            cobRsq.put("result", CARD_OPERATE_PROCESSING);
            return;
        }

        processCallBackInfo(dao, query);
    }

}

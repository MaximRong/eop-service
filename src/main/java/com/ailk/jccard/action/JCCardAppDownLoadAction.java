package com.ailk.jccard.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.phw.core.lang.Collections;
import org.phw.eop.support.EopActionException;
import org.phw.ibatis.engine.PDao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * JAVA卡平台延迟类处理
 * 
 * @author maxim
 *
 */
public class JCCardAppDownLoadAction extends JCCardCommonAction {

    private static final String CARD_OPERATE_OVER = "1";
    private static final String CARD_OPERATE_PROCESSING = "2";

    @Override
    public Object doAction() throws EopActionException {
        JSONObject reqObject = JSON.parseObject((String) getParams().get("reqBean"));
        boolean firstCardSession = reqObject.getBoolean("firstCardSession");
        if (firstCardSession) { // 如果是第一次请求, 则需要启动mina
            createJCBean(reqObject);
            startMinaClientInNewThread();
            cobRsq = new HashMap();
            cobRsq.put("hasMsg", "false");
            cobRsq.put("result", CARD_OPERATE_PROCESSING);
        } else {
            queryDataFromDB(reqObject);
        }

        return Collections.asMap("jcCardCobRsq", cobRsq);
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

        cobRsq = new HashMap();
        if (Collections.isEmpty(query)) {
            cobRsq.put("hasMsg", false);
            cobRsq.put("result", CARD_OPERATE_PROCESSING);
            return;
        }

        processCallBackInfo(dao, query);
    }

    /**
     * @param dao
     * @param query
     */
    private void processCallBackInfo(PDao dao, List<HashMap> query) {
        cobRsq.put("hasMsg", true);
        cobRsq.put("result", CARD_OPERATE_PROCESSING); // 放入是否处理完成
        ArrayList<Map> if3Beans = new ArrayList<Map>();
        for (HashMap q : query) {
            processEveryCallBackInfo(dao, if3Beans, q);
        }
        if (!Collections.isEmpty(if3Beans)) {
            cobRsq.put("if3Beans", if3Beans);
        }
    }

    /**
     * 处理每一条返回消息. 
     * @param dao
     * @param if3Beans
     * @param q
     */
    private void processEveryCallBackInfo(PDao dao, ArrayList<Map> if3Beans, HashMap q) {
        String ifNo = MapUtils.getString(q, "IF_NO");
        if ("IF1".equals(ifNo)) {
            processIf1CallBack(dao, q);
        } else {
            processCallBackMsg(if3Beans, q, ifNo);
        }
    }

    /**
     * 处理IF1 对应的返回信息
     * @param dao
     * @param q
     */
    private void processIf1CallBack(PDao dao, HashMap q) {
        HashMap if1Bean = new HashMap();
        cobRsq.put("if1Bean", if1Bean);
        if1Bean.put("result", MapUtils.getString(q, "RSP_RESULT"));
        if1Bean.put("cardProductName", MapUtils.getString(q, "CARD_PRODUCT_NAME"));
        if1Bean.put("userFlag", MapUtils.getString(q, "USERFLAG"));
        List<HashMap> beanLst = dao.select("JCCardSQL.queryIF1RsqData", MapUtils.getString(q, "ID"));
        if1Bean.put("appNum", beanLst.size());
        if1Bean.put("beanLst", beanLst);
        cobRsq.put("result", CARD_OPERATE_OVER);
    }

    /**
     * 处理返回消息
     * @param if3Beans
     * @param q
     * @param ifNo
     */
    private void processCallBackMsg(ArrayList<Map> if3Beans, HashMap q, String ifNo) {
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

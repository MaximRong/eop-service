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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * JAVA卡平台延迟类处理
 * 
 * @author maxim
 *
 */
public class JCCardDelayAction extends EopAction {

    //    private JCHeadBean headBean;
    //
    //    private JCIF1ReqBodyBean if1ReqBean;

    private Map cobRsq;

    @Override
    public Object doAction() throws EopActionException {
        JSONObject reqObject = JSON.parseObject((String) getParams().get("reqBean"));
        boolean isFirst = reqObject.getBoolean("isFirst");
        if (isFirst) { // 如果是第一次请求, 则需要启动mina
        //            createJCBean(reqObject);
            // 启动mima
            excuteMina();
        } else { // 否则从数据库中查询
            dataFromDB(reqObject);
        }

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
        List arrayList = new ArrayList();
        if1ReqBean.setProductIds(arrayList);
        System.out.println(headBean);
        System.out.println(if1ReqBean);
    }
    */

    // 运行MINA
    private void excuteMina() {
        // 启动一个线程启动mina
        new Thread() {
            @Override
            public void run() {
                // 启动mina
                //                JCCardDelayAction.startMina(headBean, if1ReqBean);
            }

        }.start();
    }

    /*
    // 启动mina
    protected static void startMina(JCHeadBean headBean2, JCIF1ReqBodyBean if1ReqBean2) {
        MinaClientConfig.setServerAddress("127.0.0.1");
        MinaClientConfig.setServerPort(9123);
        MinaClient.startClient(headBean2, if1ReqBean2);
    }
    */

    private void dataFromDB(JSONObject jsonObject) {
        final String SQL_XML = "com/ailk/sql/jccard/JCCardSQL.xml";
        String dataSource = "EcsStore";
        PDao dao = PDaoEngines.getDao(SQL_XML, dataSource);

        String sessionID = jsonObject.getString("sessionID");
        List<HashMap> query = dao.select("JCCardSQL.queryRsqData", sessionID);

        cobRsq = new HashMap();
        if (Collections.isEmpty(query)) {
            cobRsq.put("hasMsg", false);
            cobRsq.put("result", "2");
        } else {
            cobRsq.put("hasMsg", true);
            boolean isOver = false;
            ArrayList<Map> if3Beans = new ArrayList<Map>();
            for (HashMap q : query) {
                String ifNo = MapUtils.getString(q, "IF_NO");
                if ("IF1".equals(ifNo)) {// 当时if1时候
                    isOver = true;
                    HashMap if1Bean = new HashMap();
                    if1Bean.put("result", MapUtils.getString(q, "RSP_RESULT"));
                    if1Bean.put("cardProductName", MapUtils.getString(q, "CARD_PRODUCT_NAME"));
                    if1Bean.put("userFlag", MapUtils.getString(q, "USERFLAG"));
                    List beanLst = new ArrayList();
                    List<HashMap> if1Datas = dao.select("JCCardSQL.queryIF1RsqData", MapUtils.getString(q, "ID"));
                    for (HashMap data : if1Datas) {
                        beanLst.add(data);
                    }
                    if1Bean.put("beanLst", beanLst);
                    if1Bean.put("appNum", beanLst.size());
                    cobRsq.put("if1Bean", if1Bean);
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
            cobRsq.put("result", isOver ? "1" : "2"); // 放入是否处理完成
        }

    }
}

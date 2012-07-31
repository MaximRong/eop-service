package com.ailk.jccard.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.phw.core.lang.Collections;
import org.phw.core.lang.Pair;
import org.phw.eop.support.EopAction;
import org.phw.ibatis.engine.PDao;
import org.phw.ibatis.util.PDaoEngines;

import com.ailk.jccard.action.validate.JCBaseValidator;
import com.ailk.jccard.mina.MinaClient;
import com.ailk.jccard.mina.MinaClientConfig;
import com.ailk.jccard.mina.bean.JCHeadBean;
import com.ailk.jccard.mina.bean.req.JCIF1ReqBodyBean;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public abstract class JCCardCommonAction extends EopAction {

    private static int JCCARD_FACE_PORT = 9123;
    private static String JCCARD_FACE_URL = "127.0.0.1";
    protected static final String CARD_OPERATE_OVER = "1";
    protected static final String CARD_OPERATE_PROCESSING = "2";
    protected static final String CARD_OPERATE_FAIL = "0";

    static {
        PDao dao = PDaoEngines.getDao("com/ailk/sql/jccard/JCCardSQL.xml", "EcsStore");
        JCCARD_FACE_URL = dao.selectString("JCCardSQL.queryJCFaceUrl", null);
        JCCARD_FACE_PORT = dao.selectInteger("JCCardSQL.queryJCFacePort", null);
    }

    protected JCHeadBean headBean;

    protected JCIF1ReqBodyBean if1ReqBean;

    protected Map cobRsq = new HashMap();

    @Override
    public Object doAction() {
        JSONObject reqObject = JSON.parseObject((String) getParams().get("reqBean"));
        JCBaseValidator validator = new JCBaseValidator();
        Pair<Boolean, String> validateResult = validator.validate(reqObject);
        if (!validateResult.getFirst()) {
            // 这边要定义校验失败
            cobRsq.put("hasMsg", "false");
            cobRsq.put("result", CARD_OPERATE_FAIL);
            cobRsq.put("validMsg", validateResult.getSecond());
        }
        subActionProcess();
        return Collections.asMap("jcCardCobRsq", cobRsq);
    }

    protected abstract void subActionProcess();

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
        MinaClientConfig.setServerAddress(JCCARD_FACE_URL);
        MinaClientConfig.setServerPort(JCCARD_FACE_PORT);
        MinaClient.startClient(headBean, if1ReqBean);
    }

    protected PDao newDao() {
        return PDaoEngines.getDao("com/ailk/sql/jccard/JCCardSQL.xml", "EcsStore");
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

    /**
     * @param dao
     * @param query
     */
    protected void processCallBackInfo(PDao dao, List<HashMap> query) {
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
        if1Bean.put("appNum", "0");
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

package com.ailk.ess.n6ess.num;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.chinaunicom.ws.ESSNumSer.ESSNumSerProxy;
import cn.chinaunicom.ws.ESSNumSer.unibssBody.NUM_PRE_ORDER_INPUT;
import cn.chinaunicom.ws.ESSNumSer.unibssBody.NUM_PRE_ORDER_INPUTUNI_BSS_BODY;
import cn.chinaunicom.ws.ESSNumSer.unibssBody.NUM_PRE_ORDER_OUTPUT;
import cn.chinaunicom.ws.ESSNumSer.unibssBody.PreOccupyResReq.NUM_PRE_ORDER_REQ;
import cn.chinaunicom.ws.ESSNumSer.unibssBody.PreOccupyResRsp.NUM_PRE_ORDER_RSP;
import cn.chinaunicom.ws.unibssAttached.UNI_BSS_ATTACHED;
import cn.chinaunicom.ws.unibssHead.UNI_BSS_HEAD;
import cn.chinaunicom.ws.unibssHead.UNI_BSS_HEADRESPONSE;

import com.ailk.base.KeyConst;
import com.ailk.base.NumConst;
import com.ailk.ess.n6ess.utils.N6FaceUtils;

/**
 * 号码预占处理类。
 *
 * @author wanglei
 *
 * 2012-3-6
 */
public class N6PreOccupyNumProcessor implements IN6NumProcessor {
    // TODO 日志啊
    private static final Logger logger = LoggerFactory.getLogger(N6PreOccupyNumProcessor.class);

    /**
     * 号码预占处理。
     * @param inMap
     * @return
     */
    @Override
    public Map process(Map inMap) {
        logger.info("调用N6ESS号码预占接口开始。");
        NUM_PRE_ORDER_INPUT numPreOrderReq = new NUM_PRE_ORDER_INPUT();
        // 报文头
        UNI_BSS_HEAD reqHead = N6FaceUtils.buildReqHead(inMap, NumConst.N6_SERVICE_NUM, NumConst.N6_OPERATE_PRENUM);
        // 报文体
        NUM_PRE_ORDER_INPUTUNI_BSS_BODY reqBody = buildReqBody((Map) inMap.get(KeyConst.REQ_BODY));
        numPreOrderReq.setUNI_BSS_HEAD(reqHead);
        numPreOrderReq.setUNI_BSS_BODY(reqBody);
        numPreOrderReq.setUNI_BSS_ATTACHED(new UNI_BSS_ATTACHED());
        ESSNumSerProxy proxy = new ESSNumSerProxy();
        logger.info("调用N6ESS号码预占接口EndPoint：" + NumConst.N6ESS_NUMSER_ENDPOINT);
        proxy.setEndpoint(NumConst.N6ESS_NUMSER_ENDPOINT);
        NUM_PRE_ORDER_OUTPUT numPreOrderRsp = null;
        try {
            numPreOrderRsp = proxy.preOccupyRes(numPreOrderReq);
            return buildRspMsg(numPreOrderRsp);
        }
        catch (RemoteException e) {
            logger.error("调用N6ESS号码预占接口失败", e);
            return N6FaceUtils.buildRspMsg(e);
        }
    }

    /**
     * 构建请求报文体。
     * @param inMap
     * @return
     */
    private NUM_PRE_ORDER_INPUTUNI_BSS_BODY buildReqBody(Map inBody) {
        NUM_PRE_ORDER_INPUTUNI_BSS_BODY body = new NUM_PRE_ORDER_INPUTUNI_BSS_BODY();
        NUM_PRE_ORDER_REQ numPreOrderReq = new NUM_PRE_ORDER_REQ();
        numPreOrderReq.setPROVINCE_CODE((String) inBody.get(KeyConst.PROVINCE));
        numPreOrderReq.setEPARCHY_CODE((String) inBody.get(KeyConst.CITY));
        // 转码在MallApp
        numPreOrderReq.setCITY_CODE((String) inBody.get(KeyConst.DISTRICT));
        numPreOrderReq.setCHANNEL_ID((String) inBody.get(KeyConst.CHANNEL_ID));
        Object resObj = inBody.get(KeyConst.RESOURCES_INFO);
        Map resource = resObj instanceof Map ? (Map) resObj : (Map) ((List) resObj).get(0);
        numPreOrderReq.setSEQ_ID((String) resource.get(KeyConst.PROKEY));
        logger.info("调用N6ESS号码预占接口，号码：" + resource.get(KeyConst.RESOURCE_CODE));
        numPreOrderReq.setSERIAL_NUMBER((String) resource.get(KeyConst.RESOURCE_CODE));
        logger.info("调用N6ESS号码预占接口，预占失效时间：" + resource.get(KeyConst.OCCUPIED_TIME));
        numPreOrderReq.setEXPIRE_DATE((String) resource.get(KeyConst.OCCUPIED_TIME));
        body.setNUM_PRE_ORDER_REQ(numPreOrderReq);
        return body;
    }

    /**
     * 构建响应报文。
     * @param numPreOrderRsp
     * @return
     */
    private Map buildRspMsg(NUM_PRE_ORDER_OUTPUT numPreOrderRsp) {
        Map retMap = new HashMap();
        UNI_BSS_HEADRESPONSE response = numPreOrderRsp.getUNI_BSS_HEAD().getRESPONSE();
        if (!NumConst.RESP_CODE_SUCCESS.equals(response.getRSP_CODE())) {
            logger.info("调用N6ESS号码预占接口成功，RespCode=" + response.getRSP_CODE() + "，RespDesc=" + response.getRSP_DESC());
            retMap.put(KeyConst.RESP_CODE, response.getRSP_CODE());
            retMap.put(KeyConst.RESP_DESC, response.getRSP_DESC());
            return retMap;
        }
        NUM_PRE_ORDER_RSP rspBody = numPreOrderRsp.getUNI_BSS_BODY().getNUM_PRE_ORDER_RSP();
        logger.info("调用N6ESS号码预占接口成功，RespCode=" + rspBody.getRESP_CODE() + "，RespDesc=" + rspBody.getRESP_DESC());
        retMap.put(KeyConst.RESP_CODE, rspBody.getRESP_CODE());
        retMap.put(KeyConst.RESP_DESC, rspBody.getRESP_DESC());
        return retMap;
    }
}

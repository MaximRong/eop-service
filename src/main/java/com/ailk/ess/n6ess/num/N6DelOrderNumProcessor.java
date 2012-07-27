package com.ailk.ess.n6ess.num;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.chinaunicom.ws.ESSNumSer.ESSNumSerProxy;
import cn.chinaunicom.ws.ESSNumSer.unibssBody.DEL_NUM_ORDER_INPUT;
import cn.chinaunicom.ws.ESSNumSer.unibssBody.DEL_NUM_ORDER_INPUTUNI_BSS_BODY;
import cn.chinaunicom.ws.ESSNumSer.unibssBody.DEL_NUM_ORDER_OUTPUT;
import cn.chinaunicom.ws.ESSNumSer.unibssBody.delNumOrderReq.DEL_NUM_ORDER_REQ;
import cn.chinaunicom.ws.ESSNumSer.unibssBody.delNumOrderRsp.DEL_NUM_ORDER_RSP;
import cn.chinaunicom.ws.unibssAttached.UNI_BSS_ATTACHED;
import cn.chinaunicom.ws.unibssHead.UNI_BSS_HEAD;
import cn.chinaunicom.ws.unibssHead.UNI_BSS_HEADRESPONSE;

import com.ailk.base.KeyConst;
import com.ailk.base.NumConst;
import com.ailk.ess.n6ess.utils.N6FaceUtils;

/**
 * 取消号码预定处理类。
 *
 * @author wanglei
 *
 * 2012-3-6
 */
public class N6DelOrderNumProcessor implements IN6NumProcessor {
    // TODO 日志啊
    private static final Logger logger = LoggerFactory.getLogger(N6DelOrderNumProcessor.class);

    @Override
    public Map process(Map inMap) {
        logger.info("调用N6ESS删除号码预定接口开始。");
        DEL_NUM_ORDER_INPUT delNumOrderReq = new DEL_NUM_ORDER_INPUT();
        // 报文头
        UNI_BSS_HEAD reqHead = N6FaceUtils.buildReqHead(inMap, NumConst.N6_SERVICE_NUM,
                NumConst.N6_OPERATE_DELORDERNUM);
        // 报文体
        DEL_NUM_ORDER_INPUTUNI_BSS_BODY reqBody = buildReqBody((Map) inMap.get(KeyConst.REQ_BODY));
        delNumOrderReq.setUNI_BSS_HEAD(reqHead);
        delNumOrderReq.setUNI_BSS_BODY(reqBody);
        delNumOrderReq.setUNI_BSS_ATTACHED(new UNI_BSS_ATTACHED());
        ESSNumSerProxy proxy = new ESSNumSerProxy();
        logger.info("调用N6ESS删除号码预定接口EndPoint：" + NumConst.N6ESS_NUMSER_ENDPOINT);
        proxy.setEndpoint(NumConst.N6ESS_NUMSER_ENDPOINT);
        DEL_NUM_ORDER_OUTPUT delNumOrderRsp = null;
        try {
            delNumOrderRsp = proxy.delNumOrder(delNumOrderReq);
            return buildRspMsg(delNumOrderRsp);
        }
        catch (RemoteException e) {
            logger.error("调用N6ESS删除号码预定接口失败", e);
            return N6FaceUtils.buildRspMsg(e);
        }
    }

    /**
     * 构建请求报文体。
     * @param inMap
     * @return
     */
    private DEL_NUM_ORDER_INPUTUNI_BSS_BODY buildReqBody(Map inBody) {
        DEL_NUM_ORDER_INPUTUNI_BSS_BODY body = new DEL_NUM_ORDER_INPUTUNI_BSS_BODY();
        DEL_NUM_ORDER_REQ delNumOrderReq = new DEL_NUM_ORDER_REQ();
        Object resObj = inBody.get(KeyConst.RESOURCES_INFO);
        Map resource = resObj instanceof Map ? (Map) resObj : (Map) ((List) resObj).get(0);
        logger.info("调用N6ESS删除号码预定接口，号码：" + resource.get(KeyConst.RESOURCE_CODE));
        delNumOrderReq.setSERIAL_NUMBER((String) resource.get(KeyConst.RESOURCE_CODE));
        delNumOrderReq.setCERT_TYPE_CODE((String) resource.get(KeyConst.CERT_TYPE));
        delNumOrderReq.setCERT_CODE((String) resource.get(KeyConst.CERT_NUM));
        body.setDEL_NUM_ORDER_REQ(delNumOrderReq);
        return body;
    }

    /**
     * 构建响应报文。
     * @param numPreOrderRsp
     * @return
     */
    private Map buildRspMsg(DEL_NUM_ORDER_OUTPUT delNumOrderRsp) {
        Map retMap = new HashMap();
        UNI_BSS_HEADRESPONSE response = delNumOrderRsp.getUNI_BSS_HEAD().getRESPONSE();
        if (!NumConst.RESP_CODE_SUCCESS.equals(response.getRSP_CODE())) {
            logger.info("调用N6ESS删除号码预定接口成功，RespCode=" + response.getRSP_CODE() + "，RespDesc=" + response.getRSP_DESC());
            retMap.put(KeyConst.RESP_CODE, response.getRSP_CODE());
            retMap.put(KeyConst.RESP_DESC, response.getRSP_DESC());
            return retMap;
        }
        DEL_NUM_ORDER_RSP rspBody = delNumOrderRsp.getUNI_BSS_BODY().getDEL_NUM_ORDER_RSP();
        logger.info("调用N6ESS删除号码预定接口成功，RespCode=" + rspBody.getRESP_CODE() + "，RespDesc=" + rspBody.getRESP_DESC());
        retMap.put(KeyConst.RESP_CODE, rspBody.getRESP_CODE());
        retMap.put(KeyConst.RESP_DESC, rspBody.getRESP_DESC());
        return retMap;
    }

}

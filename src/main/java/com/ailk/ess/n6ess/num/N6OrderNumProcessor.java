package com.ailk.ess.n6ess.num;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.chinaunicom.ws.ESSNumSer.ESSNumSerProxy;
import cn.chinaunicom.ws.ESSNumSer.unibssBody.NUM_ORDER_INPUT;
import cn.chinaunicom.ws.ESSNumSer.unibssBody.NUM_ORDER_INPUTUNI_BSS_BODY;
import cn.chinaunicom.ws.ESSNumSer.unibssBody.NUM_ORDER_OUTPUT;
import cn.chinaunicom.ws.ESSNumSer.unibssBody.numOrderReq.NUM_ORDER_REQ;
import cn.chinaunicom.ws.ESSNumSer.unibssBody.numOrderRsp.NUM_ORDER_RSP;
import cn.chinaunicom.ws.unibssAttached.UNI_BSS_ATTACHED;
import cn.chinaunicom.ws.unibssHead.UNI_BSS_HEAD;
import cn.chinaunicom.ws.unibssHead.UNI_BSS_HEADRESPONSE;

import com.ailk.base.KeyConst;
import com.ailk.base.NumConst;
import com.ailk.ess.n6ess.utils.N6FaceUtils;

/**
 * 号码预定处理类。
 *
 * @author wanglei
 *
 * 2012-3-6
 */
public class N6OrderNumProcessor implements IN6NumProcessor {
    // TODO 日志啊
    private static final Logger logger = LoggerFactory.getLogger(N6OrderNumProcessor.class);

    @Override
    public Map process(Map inMap) {
        logger.info("调用N6ESS号码预定接口开始。");
        NUM_ORDER_INPUT numOrderReq = new NUM_ORDER_INPUT();
        // 报文头
        UNI_BSS_HEAD reqHead = N6FaceUtils.buildReqHead(inMap, NumConst.N6_SERVICE_NUM, NumConst.N6_OPERATE_ORDERNUM);
        // 报文体
        NUM_ORDER_INPUTUNI_BSS_BODY reqBody = buildReqBody((Map) inMap.get(KeyConst.REQ_BODY));
        numOrderReq.setUNI_BSS_HEAD(reqHead);
        numOrderReq.setUNI_BSS_BODY(reqBody);
        numOrderReq.setUNI_BSS_ATTACHED(new UNI_BSS_ATTACHED());
        ESSNumSerProxy proxy = new ESSNumSerProxy();
        logger.info("调用N6ESS号码预定接口EndPoint：" + NumConst.N6ESS_NUMSER_ENDPOINT);
        proxy.setEndpoint(NumConst.N6ESS_NUMSER_ENDPOINT);
        NUM_ORDER_OUTPUT numOrderRsp = null;
        try {
            numOrderRsp = proxy.numOrder(numOrderReq);
            return buildRspMsg(numOrderRsp);
        }
        catch (RemoteException e) {
            logger.error("调用N6ESS号码预定接口失败", e);
            return N6FaceUtils.buildRspMsg(e);
        }
    }

    /**
     * 构建请求报文体。
     * @param inMap
     * @return
     */
    private NUM_ORDER_INPUTUNI_BSS_BODY buildReqBody(Map inBody) {
        NUM_ORDER_INPUTUNI_BSS_BODY body = new NUM_ORDER_INPUTUNI_BSS_BODY();
        NUM_ORDER_REQ numOrderReq = new NUM_ORDER_REQ();
        numOrderReq.setPROVINCE_CODE((String) inBody.get(KeyConst.PROVINCE));
        numOrderReq.setEPARCHY_CODE((String) inBody.get(KeyConst.CITY));
        // 转码在MallApp
        numOrderReq.setCITY_CODE((String) inBody.get(KeyConst.DISTRICT));
        numOrderReq.setCHANNEL_ID((String) inBody.get(KeyConst.CHANNEL_ID));

        Object resObj = inBody.get(KeyConst.RESOURCES_INFO);
        Map resource = resObj instanceof Map ? (Map) resObj : (Map) ((List) resObj).get(0);
        logger.info("调用N6ESS号码预定接口，预占关键字(SEQ_ID)：{}", resource.get(KeyConst.PROKEY));
        numOrderReq.setSEQ_ID((String) resource.get(KeyConst.PROKEY));
        numOrderReq.setORDER_ID((String) resource.get(KeyConst.PROKEY));
        logger.info("调用N6ESS号码预定接口，号码：{}", resource.get(KeyConst.RESOURCE_CODE));
        numOrderReq.setSERIAL_NUMBER((String) resource.get(KeyConst.RESOURCE_CODE));
        logger.info("调用N6ESS号码预定接口，产品编码：{}", resource.get(KeyConst.PRODUCT_ID));
        numOrderReq.setPRODUCT_ID((String) resource.get(KeyConst.PRODUCT_ID));
        logger.info("调用N6ESS号码预定接口，预定失效时间：{}", resource.get(KeyConst.OCCUPIED_TIME));
        numOrderReq.setEXPIRE_DATE((String) resource.get(KeyConst.OCCUPIED_TIME));
        // 客户名称非空
        String custName = (String) resource.get(KeyConst.CUST_NAME);
        numOrderReq.setCUST_NAME(StringUtils.isEmpty(custName) ? "无" : custName);
        numOrderReq.setCERT_TYPE_CODE((String) resource.get(KeyConst.CERT_TYPE));
        numOrderReq.setCERT_CODE((String) resource.get(KeyConst.CERT_NUM));
        // 联系电话非空
        String contactPhone = (String) resource.get(KeyConst.CONTACT_NUM);
        numOrderReq.setPHONE(StringUtils.isEmpty(contactPhone) ? "无" : contactPhone);
        // 付费预定时1，其他根据接口传参
        String occupiedFlag = (String) resource.get(KeyConst.OCCUPIED_FLAG);
        String delayOccupiedFlag = (String) resource.get(KeyConst.DELAY_OCCUPIED_FLAG);
        if (NumConst.OCCUPY_FLAG_PAIED.equals(occupiedFlag) || NumConst.DELAY_OCCUPY_FALG_YES.equals(delayOccupiedFlag)) {
            numOrderReq.setOPER_TYPE("1"); // 延长预定时间
        }
        else {
            numOrderReq.setOPER_TYPE("0"); // 普通预定
        }

        body.setNUM_ORDER_REQ(numOrderReq);
        return body;
    }

    /**
     * 构建响应报文。
     * @param numOrderRsp
     * @return
     */
    private Map buildRspMsg(NUM_ORDER_OUTPUT numOrderRsp) {
        Map retMap = new HashMap();
        UNI_BSS_HEADRESPONSE response = numOrderRsp.getUNI_BSS_HEAD().getRESPONSE();
        if (!NumConst.RESP_CODE_SUCCESS.equals(response.getRSP_CODE())) {
            logger.info("调用N6ESS号码预定接口成功，RespCode=" + response.getRSP_CODE() + "，RespDesc=" + response.getRSP_DESC());
            retMap.put(KeyConst.RESP_CODE, response.getRSP_CODE());
            retMap.put(KeyConst.RESP_DESC, response.getRSP_DESC());
            return retMap;
        }
        NUM_ORDER_RSP rspBody = numOrderRsp.getUNI_BSS_BODY().getNUM_ORDER_RSP();
        logger.info("调用N6ESS号码预定接口成功，RespCode=" + rspBody.getRESP_CODE() + "，RespDesc=" + rspBody.getRESP_DESC());
        retMap.put(KeyConst.RESP_CODE, rspBody.getRESP_CODE());
        retMap.put(KeyConst.RESP_DESC, rspBody.getRESP_DESC());
        return retMap;
    }

}

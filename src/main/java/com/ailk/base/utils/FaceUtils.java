package com.ailk.base.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.phw.core.lang.ThreadSafeSimpleDateFormat;
import org.phw.eop.utils.XmlUtils2;
import org.w3c.dom.Element;

import cn.chinaunicom.ws.unibssHead.UNI_BSS_HEAD;
import cn.chinaunicom.ws.unibssHead.UNI_BSS_HEADCOM_BUS_INFO;
import cn.chinaunicom.ws.unibssHead.UNI_BSS_HEADROUTING;
import cn.chinaunicom.ws.unibssHead.UNI_BSS_HEADSP_RESERVE;

import com.ailk.base.KeyConst;
import com.ailk.base.NumConst;
import com.ailk.ecs.esf.base.utils.StrUtils;
import com.ailk.ecs.esf.service.eface.engine.XmlWriterEngine;
import com.linkage.base.util.MapUtils;
import com.linkage.ecssales.interfaces.req.ROUTING;
import com.linkage.ecssales.interfaces.req.SPRESERVE;
import com.linkage.ecssales.interfaces.req.UNIBSSRequestHead;

/**
 * 公共接口工具类。
 *
 * @author wanglei
 *
 * 2012-3-8
 */
public class FaceUtils {

    /**
     * 生成响应报文。
     * @param inMap
     * @param rspTemplate
     * @return
     */
    public static String createRspXml(Map inMap, String rspTemplate) {
        // 处理时间
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
        inMap.put(KeyConst.PROCESS_TIME, fmt.format(new Date()));
        // 响应描述
        String respDesc = (String) inMap.get(KeyConst.RESP_DESC);
        inMap.put(KeyConst.RESP_DESC, StringUtils.isNotEmpty(respDesc) ? StrUtils.substrb(respDesc, 100) : respDesc);
        // 报文头响应信息
        inMap.put(KeyConst.RESPONSE, createHeadResponse(inMap));
        // 生成响应报文
        XmlWriterEngine writer = new XmlWriterEngine();
        writer.parseTemplateFile(rspTemplate);
        return writer.createMsg(inMap);
    }

    /**
     * 生成报文头Response节点。
     * @param inMap
     * @return
     */
    private static Map createHeadResponse(Map inMap) {
        // 报文体RespCode节点
        String bodyRespCode = (String) inMap.get(KeyConst.RESP_CODE);
        // 报文体RespDesc节点
        String bodyRespDesc = (String) inMap.get(KeyConst.RESP_DESC);
        if (NumConst.RESP_CODE_SUCCESS.equals(bodyRespCode)) {
            return createHeadResponse(NumConst.RSP_TYPE_SUCCESS, NumConst.RESP_CODE_SUCCESS, bodyRespDesc);
        }
        return createHeadResponse(NumConst.RSP_TYPE_FAIL, NumConst.RESP_CODE_FAIL, bodyRespDesc);
    }

    /**
     * 生成报文头Response节点。
     * @param rspType
     * @param rspCode
     * @param rspDesc
     * @return
     */
    private static Map createHeadResponse(String rspType, String rspCode, String rspDesc) {
        Map response = new HashMap();
        response.put(KeyConst.RSP_TYPE, rspType);
        response.put(KeyConst.RSP_CODE, rspCode);
        response.put(KeyConst.RSP_DESC, rspDesc);
        return response;
    }

    /**
     * 解析响应报文 to EssMap。
     * @param rspXml
     * @return
     */
    public static Map parseRspXml2EssMap(String rspXml) {
        if (StringUtils.isEmpty(rspXml)) {
            return new HashMap();
        }
        Map retMap = new HashMap();
        retMap.put(KeyConst.RSP_XML, rspXml);

        Element rspRoot = XmlUtils2.getRootElementFromString(rspXml);
        retMap.put(KeyConst.RSP_HEAD, XmlUtils2.dom2Map(rspRoot));

        String rspBodyXml = XmlUtils2.getElementValue(rspRoot, KeyConst.SVC_CONT);
        if (StringUtils.isNotEmpty(rspBodyXml)) {
            Element rspBodyRoot = XmlUtils2.getRootElementFromString(rspBodyXml);
            retMap.put(KeyConst.RSP_BODY, XmlUtils2.dom2Map(rspBodyRoot));
        }
        return retMap;
    }

    /**
     * 构建北六请求报文头。
     * @param inMap
     * @param service 服务名。
     * @param operate 操作名。
     * @return
     */
    public static UNI_BSS_HEAD buildN6ReqHead(Map inMap, String service, String operate) {
        UNI_BSS_HEAD head = new UNI_BSS_HEAD();
        head.setORIG_DOMAIN(NumConst.N6_ORIG_DOMAIN);
        head.setACTION_CODE(NumConst.N6_ACTION_CODE);
        head.setACTION_RELATION(NumConst.N6_ACTION_RELATION);
        head.setMSG_RECEIVER(NumConst.N6_MSG_RECEIVER);
        head.setMSG_SENDER(NumConst.N6_MSG_SENDER);
        head.setSERVICE_NAME(service);
        head.setOPERATE_NAME(operate);

        Map inHead = (Map) inMap.get(KeyConst.REQ_HEAD);
        head.setPROC_ID((String) inHead.get(KeyConst.TRANS_IDO)); // 记录日志
        ThreadSafeSimpleDateFormat format = new ThreadSafeSimpleDateFormat("yyyyMMddHHmmss");
        head.setPROCESS_TIME(format.format(new Date()));
        head.setTEST_FLAG(NumConst.N6ESS_TESTFLAG);
        //head.setTRANS_IDH("no longer exists");
        head.setTRANS_IDO(head.getPROC_ID());

        UNI_BSS_HEADSP_RESERVE spReserve = new UNI_BSS_HEADSP_RESERVE();
        spReserve.setHSNDUNS(NumConst.N6_HSNDUNS);
        spReserve.setOSNDUNS(NumConst.N6_OSNDUNS);
        spReserve.setTRANS_IDC(head.getORIG_DOMAIN() + spReserve.getOSNDUNS() + head.getPROC_ID());
        format = new ThreadSafeSimpleDateFormat("yyyyMMdd");
        spReserve.setCUTOFFDAY(format.format(new Date()));
        spReserve.setCONV_ID(spReserve.getTRANS_IDC() + spReserve.getCUTOFFDAY());
        head.setSP_RESERVE(spReserve);

        UNI_BSS_HEADROUTING routing = new UNI_BSS_HEADROUTING();
        routing.setROUTE_TYPE(NumConst.N6_ROUTE_TYPE);
        routing.setROUTE_VALUE((String) inMap.get(KeyConst.PROVINCE));
        head.setROUTING(routing);

        UNI_BSS_HEADCOM_BUS_INFO comBusInfo = new UNI_BSS_HEADCOM_BUS_INFO();
        Map inBody = (Map) inMap.get(KeyConst.REQ_BODY);
        comBusInfo.setACCESS_TYPE((String) inBody.get(KeyConst.ACCESS_TYPE));
        comBusInfo.setCHANNEL_ID((String) inBody.get(KeyConst.CHANNEL_ID));
        comBusInfo.setCHANNEL_TYPE((String) inBody.get(KeyConst.CHANNEL_TYPE));
        comBusInfo.setCITY_CODE((String) inBody.get(KeyConst.DISTRICT));
        comBusInfo.setEPARCHY_CODE((String) inBody.get(KeyConst.CITY));
        comBusInfo.setOPER_ID(NumConst.N6_OPER_ID);
        comBusInfo.setORDER_TYPE(NumConst.N6_ORDER_TYPE);
        comBusInfo.setPROVINCE_CODE(routing.getROUTE_VALUE());
        head.setCOM_BUS_INFO(comBusInfo);
        return head;
    }

    /**
     * 构建响应信息。
     * @param e
     * @return
     */
    public static Map buildRspMsg(Exception e) {
        return buildRspMsg(NumConst.RESP_CODE_FAIL, e.getMessage());
    }

    /**
     * 构建响应信息。
     * @param e
     * @return
     */
    public static Map buildRspMsg(String respCode, String respDesc) {
        return MapUtils.asMap(KeyConst.RESP_CODE, respCode, KeyConst.RESP_DESC, respDesc);
    }

    /**
     * 构建BSS请求报文头。
     * @param inMap
     * @param bipCode
     * @param activityCode
     * @return
     */
    public static UNIBSSRequestHead buildBssReqHead(Map inMap, String bipCode, String activityCode) {
        UNIBSSRequestHead bssReqHead = new UNIBSSRequestHead();
        bssReqHead.setOrigdomain("ECIP");
        bssReqHead.setHomedomain("UCRM");
        bssReqHead.setBipcode(bipCode);
        bssReqHead.setBipver("0100");
        bssReqHead.setActivitycode(activityCode);
        bssReqHead.setActioncode("0");
        bssReqHead.setActionrelation("0");

        ROUTING routing = new ROUTING();
        routing.setRoutetype("00");
        String provice = (String) inMap.get(KeyConst.PROVINCE);
        routing.setRoutevalue(provice);
        bssReqHead.setRouting(routing);

        // ESS请求报文头
        Map essReqHead = (Map) inMap.get(KeyConst.REQ_HEAD);
        bssReqHead.setProcid((String) essReqHead.get(KeyConst.TRANS_IDO));
        bssReqHead.setTransido((String) essReqHead.get(KeyConst.TRANS_IDO));
        ThreadSafeSimpleDateFormat format = new ThreadSafeSimpleDateFormat("yyyyMMddHHmmss");
        bssReqHead.setProcesstime(format.format(new Date()));

        SPRESERVE spReserve = new SPRESERVE();
        spReserve.setHsnduns("9800");
        spReserve.setOsnduns("0002");
        spReserve.setTransidc(bssReqHead.getOrigdomain() + spReserve.getOsnduns() + bssReqHead.getProcid());
        format = new ThreadSafeSimpleDateFormat("yyyyMMdd");
        spReserve.setCutoffday(format.format(new Date()));
        spReserve.setConvid(spReserve.getTransidc() + spReserve.getCutoffday());
        bssReqHead.setSpreserve(spReserve);

        // BSS测试标志均为1测试模式
        String bssTestFalg = NumConst.N6ESS_TESTFLAG;
        bssReqHead.setTestflag(StringUtils.isEmpty(bssTestFalg) ? "1" : bssTestFalg);
        bssReqHead.setMsgsender("9801");
        bssReqHead.setMsgreceiver("9800");
        bssReqHead.setSvccontver("0100");

        return bssReqHead;
    }
}

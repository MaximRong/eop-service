package com.ailk.ess.n6ess.utils;

import java.util.Date;
import java.util.Map;

import org.phw.core.lang.ThreadSafeSimpleDateFormat;

import cn.chinaunicom.ws.unibssHead.UNI_BSS_HEAD;
import cn.chinaunicom.ws.unibssHead.UNI_BSS_HEADCOM_BUS_INFO;
import cn.chinaunicom.ws.unibssHead.UNI_BSS_HEADROUTING;
import cn.chinaunicom.ws.unibssHead.UNI_BSS_HEADSP_RESERVE;

import com.ailk.base.KeyConst;
import com.ailk.base.NumConst;
import com.linkage.base.util.MapUtils;

/**
 * N6ESS接口工具类。
 *
 * @author wanglei
 *
 * 2012-3-6
 */
public class N6FaceUtils {

    /**
     * 构建北六请求报文头。
     * @param inMap
     * @param service 服务名。
     * @param operate 操作名。
     * @return
     */
    public static UNI_BSS_HEAD buildReqHead(Map inMap, String service, String operate) {
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

}

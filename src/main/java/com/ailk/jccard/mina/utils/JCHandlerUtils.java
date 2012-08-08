package com.ailk.jccard.mina.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.phw.core.lang.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.jccard.mina.bean.AppItemBean;
import com.ailk.jccard.mina.bean.JCHeadBean;
import com.ailk.jccard.mina.bean.req.JCIF1ReqBodyBean;
import com.ailk.mall.base.utils.StringUtils;
import com.ailk.phw.frombytes.FromBytesUtils;
import com.ailk.phw.frombytes.ObjectFromBytes;
import com.ailk.phw.tobytes.ObjectToBytes;
import com.ailk.phw.tobytes.ToBytesUtils;
import com.ailk.phw.utils.ConstantUtils;
import com.ailk.phw.utils.JCConvertUtils;
import com.ailk.phw.utils.ObjectUtils;
import com.alibaba.fastjson.JSON;

public class JCHandlerUtils {

    private static Logger logger = LoggerFactory.getLogger(JCHandlerUtils.class);

    public static void parseClientRequest(byte[] bytes, SessionInfo sessionInfo) {
        ObjectFromBytes objectFromBytes = new ObjectFromBytes();
        JCHeadBean head = objectFromBytes.fromBytes(bytes, JCHeadBean.class);
        JCIF1ReqBodyBean body = objectFromBytes.fromBytes(bytes, JCIF1ReqBodyBean.class);

        sessionInfo.setIfNo(TypeUtils.getEssType(head.getTypeFlag()));
        sessionInfo.setSessionId(head.getSessionId());
        sessionInfo.setStaffId(body.getOperatorId());
        sessionInfo.setJobType(body.getJobType());

        logger.info("Client Request Head(sessionId: " + sessionInfo.getSessionId() + "): " + JSON.toJSONString(head));
        logger.info("Client Request Body(sessionId: " + sessionInfo.getSessionId() + "): " + JSON.toJSONString(body));
    }

    public static byte[] generateServerResponse(Map response, SessionInfo sessionInfo) {
        String respIfNo = StringUtils.toString(response.get("RESP_IF_NO"));
        String respContent = StringUtils.toString(response.get("RESP_CONTENT"));

        JCHeadBean head = ObjectUtils.populateBean(Collections.asMap(
                "sessionId", sessionInfo.getSessionId(), "typeFlag", TypeUtils.getJcCode(respIfNo)), JCHeadBean.class);

        Map respMap = JSON.parseObject(respContent, Map.class);
        int jobType = sessionInfo.getJobType();
        if (respIfNo.equals("IF1") && (jobType == 1 || jobType == 2 || jobType == 3)) {
            List<Map> apps = (List<Map>) respMap.get("apps");
            List<AppItemBean> appItems = new ArrayList<AppItemBean>();
            for (Map app : apps) {
                appItems.add(ObjectUtils.populateBean(app, AppItemBean.class));
            }
            respMap.put("apps", appItems);
        }
        Object body = ObjectUtils.populateBean(respMap, JCBeanUtils.getResponseClassName(respIfNo, jobType));

        return generateResponse(head, body, sessionInfo);
    }

    public static String parseServerResponse(ObjectFromBytes objectFromBytes, byte[] bytes, SessionInfo sessionInfo) {
        JCHeadBean head = objectFromBytes.fromBytes(bytes, JCHeadBean.class);
        logger.info("Server Response Head(sessionId: " + sessionInfo.getSessionId() + "):" + JSON.toJSONString(head));
        return TypeUtils.getJcType(head.getTypeFlag());
    }

    public static byte[] generateRequest(Object head, Object body, SessionInfo sessionInfo) {
        logger.info("Client Request Head(sessionId: " + sessionInfo.getSessionId() + "): " + JSON.toJSONString(head));
        logger.info("Client Request Body(sessionId: " + sessionInfo.getSessionId() + "): " + JSON.toJSONString(body));
        return generateResponse(head, body);
    }

    public static byte[] generateResponse(Object head, Object body, SessionInfo sessionInfo) {
        logger.info("Server Response Head(sessionId: " + sessionInfo.getSessionId() + "): " + JSON.toJSONString(head));
        logger.info("Server Response Body(sessionId: " + sessionInfo.getSessionId() + "): " + JSON.toJSONString(body));
        return generateResponse(head, body);
    }

    public static byte[] generateResponse(Object head, Object body) {
        ObjectToBytes objectToBytes = new ObjectToBytes();
        byte[] bytes = JCConvertUtils.addBytes(objectToBytes.toBytes(head), objectToBytes.toBytes(body));
        byte[] lenBytes = ToBytesUtils.getPrimitiveToBytes(Short.class).toBytes((short) bytes.length);
        return JCConvertUtils.addBytes(lenBytes, bytes);
    }

    public static byte[] addBufferToBytes(byte[] bytes, IoBuffer ioBuffer) {
        byte[] b = new byte[ioBuffer.limit()];
        ioBuffer.get(b);
        return JCConvertUtils.addBytes(bytes, b);
    }

    public static byte[] fetchBytesFromBuffer(byte[] bytes) {
        if (bytes.length < 2) {
            return null;
        }
        Short length = FromBytesUtils.getPrimitiveFromBytes(Short.class).setOffset(0).fromBytes(bytes);
        if (length + ConstantUtils.PrimitiveOffset.SHORT_OFFSET > bytes.length) {
            return null;
        }
        return JCConvertUtils.subBytes(bytes, ConstantUtils.PrimitiveOffset.SHORT_OFFSET, length);
    }

}

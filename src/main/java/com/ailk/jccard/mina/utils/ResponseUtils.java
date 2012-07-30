package com.ailk.jccard.mina.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ailk.jccard.mina.bean.AppItemBean;
import com.ailk.jccard.mina.bean.JCHeadBean;
import com.ailk.phw.toBytes.ObjectToBytes;
import com.ailk.phw.toBytes.ToBytesUtils;
import com.ailk.phw.utils.JCConvertUtils;
import com.ailk.phw.utils.ObjectUtils;
import com.alibaba.fastjson.JSON;

public class ResponseUtils {

    /**
     * 生成服务端请求报文头.
     */
    public static JCHeadBean generateJCHeadBean(String ifNo, SessionInfo sessionInfo) {
        JCHeadBean head = new JCHeadBean();
        head.setSessionId(sessionInfo.getSessionId());
        head.setTypeFlag(TypeUtils.getJcCode(ifNo));
        return head;
    }

    /**
     * 生成客户端请求报文头.
     */
    public static JCHeadBean generateEssHeadBean(String ifNo, SessionInfo sessionInfo) {
        JCHeadBean head = new JCHeadBean();
        head.setSessionId(sessionInfo.getSessionId());
        head.setTypeFlag(TypeUtils.getEssCode(ifNo));
        return head;
    }

    /**
     * 生成请求报文体.
     */
    public static Object generateBodyBean(String ifNo, String respContent, SessionInfo sessionInfo) {
        Map respMap = JSON.parseObject(respContent, Map.class);
        int jobType = sessionInfo.getJobType();
        if (ifNo.equals("IF1") && (jobType == 1 || jobType == 2 || jobType == 3)) {
            respMap.put("apps", generateAppLs((List<Map>) respMap.get("apps")));
        }
        return ObjectUtils.populateBean(respMap, JCBeanUtils.getResponseClassName(ifNo, jobType));
    }

    /**
     * 生成应用列表.
     */
    public static List<AppItemBean> generateAppLs(List<Map> apps) {
        List<AppItemBean> appItems = new ArrayList<AppItemBean>();
        for (Map app : apps) {
            appItems.add(ObjectUtils.populateBean(app, AppItemBean.class));
        }
        return appItems;
    }

    /**
     * 生成响应字节数组.
     */
    public static byte[] generateResponse(Object head, Object body) {
        ObjectToBytes objectToBytes = new ObjectToBytes();
        byte[] bytes = JCConvertUtils.mergeByteArray(objectToBytes.toBytes(head), objectToBytes.toBytes(body));
        byte[] lenBytes = ToBytesUtils.getToBytes(Short.class).toBytes((short) bytes.length);
        return JCConvertUtils.mergeByteArray(lenBytes, bytes);
    }

}

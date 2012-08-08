package com.ailk.jccard.mina.client;

import java.util.Arrays;

import org.phw.core.lang.Collections;

import com.ailk.jccard.mina.bean.JCHeadBean;
import com.ailk.jccard.mina.bean.req.JCIF1ReqBodyBean;
import com.ailk.jccard.mina.utils.JCHandlerUtils;
import com.ailk.jccard.mina.utils.TypeUtils;
import com.ailk.phw.utils.JCConvertUtils;
import com.ailk.phw.utils.ObjectUtils;

public class MinaClientDemo {

    public static void main(String[] args) throws Exception {
        JCHeadBean head = ObjectUtils.populateBean(Collections.asMap(
                "sessionId", "20120726001122709348", "typeFlag", TypeUtils.getEssCode("IF1")), JCHeadBean.class);
        JCIF1ReqBodyBean body = ObjectUtils.populateBean(Collections.asMap(
                "operatorId", "98", "province", "98", "city", "980", "district", "9800000000",
                "channelCode", "1001", "channelType", "1", "identityCode", "1234567890",
                "msisdn", "qwertyuiop0", "iccid", "asdfghjkl0", "imsi", "zxcvbnm0",
                "operateType", 2, "productIds", Arrays.asList("900001", "900002"),
                "jobType", 5, "requestData", "IF1 Request Data"
                ), JCIF1ReqBodyBean.class);
        byte[] bytes = JCHandlerUtils.generateResponse(head, body);
        byte[] subBytes1 = JCConvertUtils.subBytes(bytes, 0, 40);
        byte[] subBytes2 = JCConvertUtils.subBytes(bytes, 40);
        byte[] nullBytes = JCConvertUtils.nullBytes();
        MinaClient client = new MinaClient();
        client.sendMessage(subBytes1);
        Thread.sleep(10000);
        client.sendMessage(JCConvertUtils.addBytes(subBytes2, nullBytes));
        // client.sendMessage(nullBytes);
        client.awaitClose();
    }

}

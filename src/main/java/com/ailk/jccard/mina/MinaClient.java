package com.ailk.jccard.mina;

import java.net.InetSocketAddress;
import java.util.Arrays;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.phw.core.lang.Collections;

import com.ailk.jccard.mina.bean.JCHeadBean;
import com.ailk.jccard.mina.bean.req.JCIF1ReqBodyBean;
import com.ailk.jccard.mina.utils.ResponseUtils;
import com.ailk.jccard.mina.utils.TypeUtils;
import com.ailk.phw.utils.ObjectUtils;

public class MinaClient {
    // TODO 不应该是静态变量
    static ConnectFuture cf = null;
    // TODO 不应该是静态变量
    static NioSocketConnector connector = null;

    public static void getconnect(String address, int port) {
        connector = new NioSocketConnector();
        connector.getFilterChain().addLast("logger", new LoggingFilter());
        connector.setConnectTimeoutMillis(1000);
        connector.setHandler(new MinaClientHandler());
        cf = connector.connect(new InetSocketAddress(address, port));
        cf.awaitUninterruptibly();
    }

    public static void sendMessage(Object content) {
        cf.getSession().write(content);
    }

    public static void awaitClose() {
        cf.getSession().getCloseFuture().awaitUninterruptibly();
        connector.dispose();
    }

    public static void startClient(JCHeadBean head, Object body) {
        getconnect(MinaClientConfig.getServerAddress(), MinaClientConfig.getServerPort());
        sendMessage(IoBuffer.wrap(ResponseUtils.generateResponse(head, body)));
        awaitClose();
    }

    public static void main(String[] args) {
        getconnect("127.0.0.1", 9123);
        JCHeadBean head = new JCHeadBean();
        head.setSessionId("20120726001122709348");
        head.setTypeFlag(TypeUtils.getEssCode("IF1"));
        JCIF1ReqBodyBean body = ObjectUtils.populateBean(Collections.asMap(
                "operatorId", "98", "province", "98", "city", "980", "district", "9800000000",
                "channelCode", "1001", "channelType", "1", "identityCode", "1234567890",
                "msisdn", "qwertyuiop0", "iccid", "asdfghjkl0", "imsi", "zxcvbnm0",
                "operateType", 2, "productIds", Arrays.asList("900001", "900002"),
                "jobType", 5, "requestData", "IF1 Request Data"
                ), JCIF1ReqBodyBean.class);
        sendMessage(IoBuffer.wrap(ResponseUtils.generateResponse(head, body)));
        cf.getSession().getCloseFuture().awaitUninterruptibly();
        connector.dispose();
    }
}

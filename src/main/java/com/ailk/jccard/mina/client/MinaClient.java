package com.ailk.jccard.mina.client;

import static org.phw.config.impl.PhwConfigMgrFactory.getConfigMgr;

import java.net.InetSocketAddress;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.ailk.jccard.mina.utils.JCHandlerUtils;

public class MinaClient {

    private ConnectFuture cf = null;

    private NioSocketConnector connector = null;

    public MinaClient() {
        this(getConfigMgr().getString("MinaServerAddress", "127.0.0.1"), getConfigMgr().getInt("MinaServerPort", 9123));
    }

    public MinaClient(String address, int port) {
        this(address, port, new MinaClientHandler());
    }

    public MinaClient(String address, int port, MinaClientHandler handler) {
        connector = new NioSocketConnector();
        connector.getFilterChain().addLast("logger", new LoggingFilter());
        connector.setConnectTimeoutMillis(1000);
        connector.setHandler(handler);
        cf = connector.connect(new InetSocketAddress(address, port));
        cf.awaitUninterruptibly();
    }

    public void awaitClose() {
        cf.getSession().getCloseFuture().awaitUninterruptibly();
        connector.dispose();
    }

    public void sendMessage(byte[] message) {
        cf.getSession().write(IoBuffer.wrap(message));
    }

    public void sendMessage(Object head, Object body) {
        sendMessage(JCHandlerUtils.generateResponse(head, body));
    }

}

package com.ailk.jccard.action;

import java.net.InetSocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class MinaClient {

    private ConnectFuture cf = null;

    private NioSocketConnector connector = null;

    public MinaClient(String address, int port) {
        connector = new NioSocketConnector();
        connector.getFilterChain().addLast("logger", new LoggingFilter());
        connector.setConnectTimeoutMillis(1000);
        connector.setHandler(new MinaClientHandler());
        cf = connector.connect(new InetSocketAddress(address, port));
        cf.awaitUninterruptibly();
    }

    public void sendMessage(Object content) {
        cf.getSession().write(content);
    }

    public void awaitClose() {
        cf.getSession().getCloseFuture().awaitUninterruptibly();
        connector.dispose();
    }

}

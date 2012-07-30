package com.ailk.jccard.mina;

import static org.phw.config.impl.PhwConfigMgrFactory.getConfigMgr;

public class MinaClientConfig {

    private static String serverAddress = "127.0.0.1";

    private static int serverPort = getConfigMgr().getInt("MinaServerPort", 9123);

    public static String getServerAddress() {
        return serverAddress;
    }

    public static void setServerAddress(String address) {
        serverAddress = address;
    }

    public static int getServerPort() {
        return serverPort;
    }

    public static void setServerPort(int port) {
        serverPort = port;
    }

}

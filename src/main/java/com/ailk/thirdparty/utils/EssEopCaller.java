package com.ailk.thirdparty.utils;

import static org.phw.config.impl.PhwConfigMgrFactory.getConfigMgr;

import org.phw.eop.api.ApiException;
import org.phw.eop.api.EopClient;
import org.phw.eop.api.Req;
import org.phw.eop.api.Rsp;

public class EssEopCaller {
    // EssEop Url
    public static final String ESSEOP_URL = getConfigMgr().getString("EssEopUrl");
    // MallEop App Info
    public static final String MALLEOP_APPCODE = getConfigMgr().getString("MallEopAppCode");
    public static final String MALLEOP_SIGNKEY = getConfigMgr().getString("MallEopSignKey");
    public static final String MALLEOP_SIGNALGORITHM = getConfigMgr().getString("MallEopSignAlgorithm");
    public static final String MALLEOP_PARAMKEY = getConfigMgr().getString("MallEopParamKey");
    public static final String MALLEOP_PARAMALGORITHM = getConfigMgr().getString("MallEopParamAlgorithm");
    public static final String MALLEOP_FORMAT = getConfigMgr().getString("MallEopFormat");

    /**
     * 调用EssEop。
     * @param <T>
     * @param request
     * @return
     * @throws ApiException
     */
    public static <T extends Rsp> T doCall(Req<T> request) throws ApiException {
        EopClient client = new EopClient(ESSEOP_URL, MALLEOP_APPCODE, MALLEOP_SIGNKEY);
        client.setSignAlgorithm(MALLEOP_SIGNALGORITHM);
        client.setParamKey(MALLEOP_PARAMKEY);
        client.setParamAlgorithm(MALLEOP_PARAMALGORITHM);
        client.setFormat(MALLEOP_FORMAT);
        T rsp = client.execute(request);
        return rsp;
    }

}

package com.ailk.ess.g3ess;

import static org.phw.config.impl.PhwConfigMgrFactory.getConfigMgr;

import java.util.Map;

import org.phw.eop.support.EopAction;
import org.phw.eop.support.EopActionException;
import org.phw.eop.utils.Strings;
import org.phw.eop.utils.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.base.KeyConst;
import com.ailk.base.utils.FaceUtils;
import com.ailk.ecs.esf.base.utils.MapUtils;
import com.ailk.ess.g3ess.ejb.MallCall;
import com.ailk.ess.g3ess.ejb.MallTestCall;
import com.alibaba.fastjson.JSON;

public class G3EssCmnAction extends EopAction {
    private static final Logger logger = LoggerFactory.getLogger(G3EssCmnAction.class);
    private static final String ESS_SRV_TARGET = "com.ailk.ess.websrv.mall.MallTradeDispatcher.dispacth";
    private static final String TEST_ENV = getConfigMgr().getString("G3Ess.EnvFlag");

    @Override
    public Object doAction() throws EopActionException {
        logger.info("3GESS请求参数：" + getParams());
        String reqXml = "";
        if (get("ReqParam") != null) {
            reqXml = XmlUtils.jsonToXml(JSON.toJSONString(get("ReqParam")), "UniBSS");
        }
        else {
            reqXml = createReqXml();
        }
        // 生成请求报文
        // String reqXml = createReqXml();
        logger.info("3GESS请求报文：" + reqXml);

        // 调用EJB
        Map rspMap = null;
        if ("1".equalsIgnoreCase(TEST_ENV)) {
            rspMap = new MallTestCall(getStr(KeyConst.PROVINCE)).call(ESS_SRV_TARGET,
                    MapUtils.asMap(KeyConst.REQ_XML, reqXml));
        }
        else {
            rspMap = new MallCall(getStr(KeyConst.PROVINCE)).call(ESS_SRV_TARGET,
                    MapUtils.asMap(KeyConst.REQ_XML, reqXml));
        }
        logger.info("3GESS响应报文：" + rspMap.get("RSP_XML"));
        return FaceUtils.parseRspXml2EssMap((String) rspMap.get("RSP_XML"));
    }

    /**
     * 生成请求报文。
     * @param params
     * @return
     */
    public String createReqXml() {
        String headXml = XmlUtils.jsonToXml(JSON.toJSONString(get(KeyConst.REQ_HEAD)), getStr(KeyConst.HEAD_ROOT));
        String bodyXml = XmlUtils.jsonToXml(JSON.toJSONString(get(KeyConst.REQ_BODY)), getStr(KeyConst.BODY_ROOT));
        headXml = Strings.replaceOnce(headXml, "<SvcCont>", "<SvcCont><![CDATA[");
        return Strings.replaceOnce(headXml, "</SvcCont>", bodyXml + "]]></SvcCont>");
    }

}

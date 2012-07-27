package com.ailk.thirdparty.utils;

import java.util.HashMap;
import java.util.Map;

import org.phw.eop.api.Req;
import org.phw.eop.api.Rsp;
import org.phw.eop.api.internal.util.XmlUtils;
import org.w3c.dom.Element;

import com.ailk.base.KeyConst;
import com.ailk.ecs.esf.service.eface.engine.XmlReaderEngine;

/**
 * 请求信息处理类。
 *
 * @author wanglei
 *
 * 2012-2-17
 */
public class ReqProcessor {

    /**
     * 校验请求报文。
     * @param xml 请求报文。
     * @param template 请求报文模板路径。
     * @return 扁平化Map。
     */
    public static Map checkReqXml(String xml, String template) {
        XmlReaderEngine reader = new XmlReaderEngine();
        reader.parseTemplateFile(template);
        return (Map) reader.createMap(xml);
    }

    /**
     * 解析请求报文。
     * @param xml 请求报文。
     * @return 符合ESS-EOP规范的Map。
     */
    public static Map parseReqXml(String xml) {
        Map retMap = new HashMap();
        try {
            Element root = XmlUtils.getRootElementFromString(xml);
            Element body = XmlUtils.getRootElementFromString(XmlUtils.getElementValue(root, KeyConst.SVC_CONT));
            XmlUtils.writeElementText(root, KeyConst.SVC_CONT, "");
            retMap.put(KeyConst.HEAD_ROOT, root.getTagName());
            retMap.put(KeyConst.REQ_HEAD, XmlUtils.dom2Map(root));
            retMap.put(KeyConst.BODY_ROOT, body.getTagName());
            retMap.put(KeyConst.REQ_BODY, XmlUtils.dom2Map(body));
        }
        catch (Throwable t) {
            throw new RuntimeException("解析请求报文异常：" + t.getMessage());
        }
        return retMap;
    }

    /**
     * 请求报文转换EopBean。
     * @param <T>
     * @param req
     * @param xml
     * @param province
     */
    public static <T extends Rsp> void parse2EopBean(Req<T> req, String xml, String province) {
        Map inMap = ReqProcessor.parseReqXml(xml);
        inMap.put(KeyConst.PROVINCE, province);
        MapToBean converter = new MapToBean();
        converter.convert(inMap, req);
    }

}

package org.phw.eop.utils;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class JsonUtils {

    public static JSONObject docToJson(Document doc) {
        JSONObject jsonObject = new JSONObject();
        walkXml(doc.getRootElement(), jsonObject);
        return jsonObject;
    }

    public static String xmlToJSON(String xmlString) throws Exception {
        Document document = DocumentHelper.parseText(xmlString);
        JSONObject jsonObject = new JSONObject();
        walkXml(document.getRootElement(), jsonObject);
        return jsonObject.toJSONString();
    }

    private static void walkXml(Element el, JSONObject jsonObject) {
        List<Element> elements = el.elements();
        if (elements.size() == 0) {
            jsonObject.put(el.getName(), el.getText());
        }
        else {
            JSONObject newJson = new JSONObject();
            jsonObject.put(el.getName(), newJson);
            for (Element childel : elements) {
                walkXml(childel, newJson);
            }
        }
    }

    public static JSON parseJSON(String rsp) {
        try {
            return JSON.parseObject(rsp);
        }
        catch (Throwable ex) {

        }

        try {
            return JSON.parseArray(rsp);
        }
        catch (Throwable ex) {

        }

        return null;
    }
}

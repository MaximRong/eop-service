package org.phw.eop.utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.xml.sax.InputSource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class XmlUtils {
    public static Document parseXML(String rsp) {
        try {
            return DocumentHelper.parseText(rsp);
        }
        catch (DocumentException e) {
            return null;
        }
    }

    public static String getElementValue(Element element) {
        return "";
    }

    public static Document jsonToDoc(JSON json) {
        Document doc = DocumentHelper.createDocument();
        Element root = doc.addElement("rspmsg");

        walkJSON(json, root);

        return doc;
    }

    private static void walkJSON(JSON json, Element element) {
        if (json instanceof JSONObject) {
            walkJSON((JSONObject) json, element);
        }
        else if (json instanceof JSONArray) {
            JSONArray arr = (JSONArray) json;

            for (Object object : arr) {
                Element itemElement = element.addElement("item");
                if (object instanceof JSON) {
                    walkJSON((JSON) object, itemElement);
                }
                else {
                    itemElement.setText(String.valueOf(object));
                }
            }
        }

    }

    public static String jsonToXml(String jsonString, String rootName) {
        Document doc = DocumentHelper.createDocument();
        Element root = doc.addElement(StringUtils.isEmpty(rootName) ? "rspmsg" : rootName);

        JSONObject jsonObj = JSON.parseObject(jsonString);
        walkJSON(jsonObj, root);

        return formatDoc(doc);
    }

    private static void walkJSON(JSONObject jsonObj, Element element) {
        Set<Entry<String, Object>> entrySet = jsonObj.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            final Object value = entry.getValue();
            final String key = entry.getKey();
            walkItem(element, value, key);
        }
    }

    private static void walkItem(Element element, final Object value, String key) {
        Element addElement = null;
        if (value instanceof JSONObject) {
            addElement = element.addElement(key);
            walkJSON((JSONObject) value, addElement);
        }
        else if (value instanceof JSONArray) {
            for (Object val : (JSONArray) value) {
                addElement = element.addElement(key);
                if (val instanceof JSONObject) {
                    walkJSON((JSONObject) val, addElement);
                }
                else if (val instanceof JSONArray) {
                    walkItem(addElement, val, "items");
                }
                else {
                    walkItem(addElement, val, "item");
                }
            }
        }
        else {
            addElement = element.addElement(key);
            addElement.setText("" + value);
        }
    }

    public static String formatDoc(Document doc) {
        OutputFormat format = OutputFormat.createCompactFormat();
        format.setTrimText(false);
        // format.setNewLineAfterDeclaration(false);
        //        format.setSuppressDeclaration(true);

        StringWriter swriter = new StringWriter();
        XMLWriter writer = new XMLWriter(swriter, format);
        try {
            writer.write(doc);
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return swriter.toString();

    }

    public static boolean isXMLFormat(String rsp) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbd = dbf.newDocumentBuilder();
            dbd.parse(new InputSource(new StringReader(rsp)));
            return true;
        }
        catch (Throwable e) {
        }

        return false;
    }

}

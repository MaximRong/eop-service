package org.phw.eop.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EopUtils {
    /**
     * 解析日期。
     * @param value 字符串
     * @return 日期 
     */
    public static Date parse(String value) {
        try {
            return new Date(EopConst.FMT_YYYYMMDDHHMMSS.parse(value).getTime());
        }
        catch (ParseException e) {
            return null;
        }
    }

    public static String urlDecode(String val) {
        try {
            return URLDecoder.decode(val, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {

        }
        return null;
    }

    public static Map<String, String> parseQueryString(String query) {
        Map<String, String> params = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String[] pair = param.split("=", -1);
            String key = urlDecode(pair[0]);
            if (Strings.isEmpty(key)) {
                continue;
            }
            String value = urlDecode(pair[1]);
            if (Strings.isEmpty(value)) {
                continue;
            }

            params.put(key, value);
        }

        return params;
    }

    public static Map<String, List<String>> parseQueryStrings(String query) {
        Map<String, List<String>> params = new HashMap<String, List<String>>();
        for (String param : query.split("&")) {
            String[] pair = param.split("=", -1);
            String key = urlDecode(pair[0]);
            if (Strings.isEmpty(key)) {
                continue;
            }
            String value = urlDecode(pair[1]);
            if (Strings.isEmpty(value)) {
                continue;
            }

            List<String> values = params.get(key);
            if (values == null) {
                values = new ArrayList<String>();
                params.put(key, values);
            }
            values.add(value);
        }
        return params;
    }

    /**
     * 获取异常堆栈信息。
     * @param e 异常。
     * @return 堆栈信息字符串。
     */
    public static String ExceptionStackAsString(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}

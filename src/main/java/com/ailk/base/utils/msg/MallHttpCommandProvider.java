package com.ailk.base.utils.msg;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sinovatech.ntf.client.ICallBack;
import com.sinovatech.ntf.client.IResult;

public class MallHttpCommandProvider {
    private static final Log logger = LogFactory.getLog(MallHttpCommandProvider.class);
    private static ResourceBundle rb = ResourceBundle.getBundle("ntfplatservice");

    private String methodType = "GET";

    private String encoding = "UTF-8";

    private Map<String, String> headParams = new HashMap();

    private Map<String, String> params = new HashMap();
    private String requestBody;
    private String url;
    private int responseCode = -1;
    private String responseContent;
    private static MultiThreadedHttpConnectionManager manager = new MultiThreadedHttpConnectionManager();

    static {
        manager.setMaxConnectionsPerHost(5);
        manager.setMaxTotalConnections(20);
    }

    public IResult send(ICallBack callBack)
            throws IOException {
        if ("POST".equalsIgnoreCase(methodType)) {
            responseContent = post();
        } else if ("GET".equalsIgnoreCase(methodType)) {
            responseContent = get();
        }
        IResult result = callBack.call(responseCode, responseContent);
        return result;
    }

    public String send() throws IOException {
        logger.info("request method : " + methodType);
        if ("POST".equalsIgnoreCase(methodType)) {
            responseContent = post();
        } else if ("GET".equalsIgnoreCase(methodType)) {
            responseContent = get();
        }

        return responseContent;
    }

    private String post()
            throws IOException {
        PostMethod method = new PostMethod(url);
        method.setRequestHeader("Connection", "close");
        int number = AgainNumber();
        method.getParams().setParameter("http.protocol.content-charset", encoding);
        method.getParams().setParameter("http.method.retry-handler", new DefaultHttpMethodRetryHandler(number, false));

        addHeadParams(method);

        if (params != null && params.size() > 1) {
            Iterator paramsIT = params.keySet().iterator();
            while (paramsIT.hasNext()) {
                String key = (String) paramsIT.next();
                String value = params.get(key);

                method.addRequestHeader(key, value);
            }

        }

        if (requestBody != null && !"".equals(requestBody.trim())) {
            logger.info("request body : " + requestBody);
            method.setRequestBody(requestBody);
            method.setRequestContentLength(requestBody.length());
        }

        try {
            return executeMethod(method);
        } catch (Exception e) {
            logger.error("POST request error : ", e);
        }
        return null;
    }

    private String get()
            throws IOException {
        GetMethod method = new GetMethod(url);
        method.setRequestHeader("Connection", "close");
        int number = AgainNumber();
        method.getParams().setParameter("http.method.retry-handler", new DefaultHttpMethodRetryHandler(number, false));

        addHeadParams(method);
        try {
            return executeMethod(method);
        } catch (Exception e) {
        }
        return null;
    }

    private int AgainNumber()
            throws IOException {
        String num = rb.getString("Retrying");

        int Retrying = Integer.parseInt(num);
        return Retrying;
    }

    private String executeMethod(HttpMethod method)
            throws IOException {
        try {
            HttpClient httpClient = new HttpClient(manager);

            httpClient.executeMethod(method);

            responseCode = method.getStatusCode();
            logger.info("response code : " + responseCode);
            logger.info("response encoding : " + encoding);

            String result = IOUtils.toString(method.getResponseBodyAsStream(),
                    encoding);

            String str1 = result;
            return str1;
        } catch (HttpException he) {
            throw he;
        } catch (IOException ioe) {
            throw ioe;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            method.releaseConnection();
        }
    }

    private void addHeadParams(HttpMethod method) {
        if (headParams != null && headParams.size() > 1) {
            Iterator headParamIT = headParams.keySet().iterator();
            while (headParamIT.hasNext()) {
                String key = (String) headParamIT.next();
                String value = headParams.get(key);

                method.addRequestHeader(key, value);
            }
        }
    }

    public MallHttpCommandProvider setPostMethod() {
        methodType = "POST";
        return this;
    }

    public MallHttpCommandProvider setGetMethod() {
        methodType = "GET";
        return this;
    }

    public MallHttpCommandProvider setHeadParams(Map<String, String> headParams) {
        this.headParams = headParams;
        return this;
    }

    public MallHttpCommandProvider setParams(Map<String, String> params) {
        this.params = params;
        return this;
    }

    public MallHttpCommandProvider addParam(String key, String value) {
        if (params == null) {
            params = new HashMap();
        }
        params.put(key, value);
        return this;
    }

    public MallHttpCommandProvider addHeadParam(String key, String value) {
        if (headParams == null) {
            headParams = new HashMap();
        }
        headParams.put(key, value);
        return this;
    }

    public MallHttpCommandProvider setEncoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    public MallHttpCommandProvider setRequestBody(String requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    public MallHttpCommandProvider setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getMethodType() {
        return methodType;
    }

    public String getEncoding() {
        return encoding;
    }

    public String getUrl() {
        return url;
    }
}

package com.ailk.jccard.mina.utils;

public class JCBeanUtils {

    public static String getResponseClassName(String ifNo, int jobType) {
        String className = "";
        if (ifNo.equals("IF1")) {
            if (jobType == 1) {
                className = "com.ailk.jccard.mina.bean.req.JCIF1Rsp01BodyBean";
            } else if (jobType == 2 || jobType == 3) {
                className = "com.ailk.jccard.mina.bean.rsp.JCIF1Rsp02BodyBean";
            } else {
                className = "com.ailk.jccard.mina.bean.rsp.JCIF1Rsp04BodyBean";
            }
        } else {
            className = "com.ailk.jccard.mina.bean.req.JC" + ifNo + "ReqBodyBean";
        }
        return className;
    }

}

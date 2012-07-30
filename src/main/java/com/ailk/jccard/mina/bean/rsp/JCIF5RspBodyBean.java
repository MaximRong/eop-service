package com.ailk.jccard.mina.bean.rsp;

import com.ailk.phw.annotations.JCBytes;
import com.ailk.phw.enums.JCExpType;

public class JCIF5RspBodyBean {

    private short resultCode;

    @JCBytes(length = 10)
    private String iccid;

    @JCBytes(length = 8)
    private String imsi;

    @JCBytes(type = JCExpType.ASCII, length = 11)
    private String msisdn;

    @JCBytes(length = 2)
    private String userIdType;

    @JCBytes(type = JCExpType.ASCII)
    private String userId;

    public void setResultCode(short resultCode) {
        this.resultCode = resultCode;
    }

    public short getResultCode() {
        return resultCode;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    public String getIccid() {
        return iccid;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getImsi() {
        return imsi;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setUserIdType(String userIdType) {
        this.userIdType = userIdType;
    }

    public String getUserIdType() {
        return userIdType;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

}

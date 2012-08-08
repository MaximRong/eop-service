package com.ailk.jccard.mina.bean.rsp;

import java.util.List;

import com.ailk.jccard.mina.bean.AppItemBean;
import com.ailk.phw.annotations.JCBytes;
import com.ailk.phw.utils.ConstantUtils;

public class JCIF1Rsp01BodyBean {

    private short resultCode;

    @JCBytes(charset = ConstantUtils.CHARSET_UNICODE)
    private String merchantName;

    @JCBytes(length = 3)
    private String availSpace;

    private byte userFlag;

    @JCBytes(lenBytes = 2)
    private List<AppItemBean> apps;

    public void setResultCode(short resultCode) {
        this.resultCode = resultCode;
    }

    public short getResultCode() {
        return resultCode;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setAvailSpace(String availSpace) {
        this.availSpace = availSpace;
    }

    public String getAvailSpace() {
        return availSpace;
    }

    public void setUserFlag(byte userFlag) {
        this.userFlag = userFlag;
    }

    public byte getUserFlag() {
        return userFlag;
    }

    public void setApps(List<AppItemBean> apps) {
        this.apps = apps;
    }

    public List<AppItemBean> getApps() {
        return apps;
    }

}

package com.ailk.jccard.mina.bean;

import com.ailk.phw.annotations.JCBytes;
import com.ailk.phw.utils.ConstantUtils;

public class AppItemBean {

    @JCBytes(charset = ConstantUtils.CHARSET_UNICODE)
    private String appName;

    private String appAid;

    private short appSize;

    private byte appOperateType;

    @JCBytes(charset = ConstantUtils.CHARSET_UNICODE)
    private String provider;

    @JCBytes(length = 8)
    private String productId;

    private String feeDesc;

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppAid(String appAid) {
        this.appAid = appAid;
    }

    public String getAppAid() {
        return appAid;
    }

    public void setAppSize(short appSize) {
        this.appSize = appSize;
    }

    public short getAppSize() {
        return appSize;
    }

    public void setAppOperateType(byte appOperateType) {
        this.appOperateType = appOperateType;
    }

    public byte getAppOperateType() {
        return appOperateType;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProvider() {
        return provider;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductId() {
        return productId;
    }

    public void setFeeDesc(String feeDesc) {
        this.feeDesc = feeDesc;
    }

    public String getFeeDesc() {
        return feeDesc;
    }

}

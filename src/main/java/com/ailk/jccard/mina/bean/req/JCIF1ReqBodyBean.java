package com.ailk.jccard.mina.bean.req;

import java.util.List;

import com.ailk.phw.annotations.JCBytes;
import com.ailk.phw.enums.JCExpType;

public class JCIF1ReqBodyBean {

    @JCBytes(type = JCExpType.ASCII)
    private String operatorId;

    @JCBytes(type = JCExpType.ASCII, length = 2)
    private String province;

    @JCBytes(type = JCExpType.ASCII, length = 3)
    private String city;

    @JCBytes(type = JCExpType.ASCII, length = 10)
    private String district;

    @JCBytes(type = JCExpType.ASCII)
    private String channelCode;

    @JCBytes(type = JCExpType.ASCII)
    private String channelType;

    @JCBytes(type = JCExpType.ASCII)
    private String identityCode;

    @JCBytes(type = JCExpType.ASCII, length = 11)
    private String msisdn;

    @JCBytes(length = 10)
    private String iccid;

    @JCBytes(length = 8)
    private String imsi;

    private byte operateType;

    @JCBytes(length = 8)
    private List<String> productIds;

    private byte jobType;

    private String requestData;

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getProvince() {
        return province;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDistrict() {
        return district;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setIdentityCode(String identityCode) {
        this.identityCode = identityCode;
    }

    public String getIdentityCode() {
        return identityCode;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getMsisdn() {
        return msisdn;
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

    public void setOperateType(byte operateType) {
        this.operateType = operateType;
    }

    public byte getOperateType() {
        return operateType;
    }

    public void setProductIds(List<String> productIds) {
        this.productIds = productIds;
    }

    public List<String> getProductIds() {
        return productIds;
    }

    public void setJobType(byte jobType) {
        this.jobType = jobType;
    }

    public byte getJobType() {
        return jobType;
    }

    public void setRequestData(String requestData) {
        this.requestData = requestData;
    }

    public String getRequestData() {
        return requestData;
    }

}

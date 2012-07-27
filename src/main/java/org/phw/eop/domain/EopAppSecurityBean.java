package org.phw.eop.domain;

import java.io.Serializable;
import java.util.Date;

import org.phw.eop.sec.support.SecurityBaseSupport;

public class EopAppSecurityBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String appid, type, algorithm, pubkey, prikey, remark, ver;
    private Date effective, expired;
    private SecurityBaseSupport securityBean;

    @Override
    public String toString() {
        return "EopAppSecurityBean [appid=" + appid + ", type=" + type + ", algorithm=" + algorithm
                + ", pubkey=" + pubkey + ", prikey=" + prikey + ", remark=" + remark + ", seq="
                + ver + ", effective=" + effective + ", expired=" + expired + ", securityBean="
                + securityBean + "]";
    }

    public boolean isValid(Date today) {
        if (effective != null && today.compareTo(effective) < 0) {
            return false; // 还未生效
        }
        if (expired != null && today.compareTo(expired) > 0) {
            return false; // 已经失效
        }

        return true;
    }

    public String getAppid() {
        return appid;
    }

    public String getType() {
        return type;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getPubkey() {
        return pubkey;
    }

    public String getPrikey() {
        return prikey;
    }

    public String getRemark() {
        return remark;
    }

    public Date getEffective() {
        return effective;
    }

    public Date getExpired() {
        return expired;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public void setPubkey(String pubkey) {
        this.pubkey = pubkey;
    }

    public void setPrikey(String prikey) {
        this.prikey = prikey;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setEffective(Date effective) {
        this.effective = effective;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }

    public void setSecurityBean(SecurityBaseSupport securityBean) {
        this.securityBean = securityBean;
    }

    public SecurityBaseSupport getSecurityBean() {
        return securityBean;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

}

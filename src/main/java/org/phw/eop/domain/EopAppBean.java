package org.phw.eop.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class EopAppBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String appcode, appid, remark, format;
    private boolean sign, mock;
    private Date effective, expired;
    private List<EopAppSecurityBean> signSecs;
    private List<EopAppSecurityBean> paramSecs;
    private List<EopRoleBean> roles;

    @Override
    public String toString() {
        return "EopAppBean [appcode=" + appcode + ", appid=" + appid + ", remark=" + remark
                + ", format=" + format + ", sign=" + sign + ", mock=" + mock + ", effective="
                + effective + ", expired=" + expired + ", signSecs="
                + signSecs + ", paramSecs=" + paramSecs + ", roles=" + roles + "]";
    }

    public EopAppSecurityBean getParamSec(long reqTs) {
        Date today = new Date(reqTs);
        for (EopAppSecurityBean secBean : paramSecs) {
            if (secBean.isValid(today)) {
                return secBean;
            }
        }
        return null;
    }

    public EopAppSecurityBean getSignSec(long reqTs) {
        Date today = new Date(reqTs);
        for (EopAppSecurityBean secBean : signSecs) {
            if (secBean.isValid(today)) {
                return secBean;
            }
        }
        return null;
    }

    public String getAppcode() {
        return appcode;
    }

    public String getAppid() {
        return appid;
    }

    public String getRemark() {
        return remark;
    }

    public boolean isSign() {
        return sign;
    }

    public Date getEffective() {
        return effective;
    }

    public Date getExpired() {
        return expired;
    }

    public boolean isMock() {
        return mock;
    }

    public String getFormat() {
        return format;
    }

    public void setAppcode(String appcode) {
        this.appcode = appcode;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setSign(boolean sign) {
        this.sign = sign;
    }

    public void setEffective(Date effective) {
        this.effective = effective;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }

    public void setMock(boolean mock) {
        this.mock = mock;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setSignSecs(List<EopAppSecurityBean> signSecs) {
        this.signSecs = signSecs;
    }

    public List<EopAppSecurityBean> getSignSecs() {
        return signSecs;
    }

    public void setParamSecs(List<EopAppSecurityBean> paramSecs) {
        this.paramSecs = paramSecs;
    }

    public List<EopAppSecurityBean> getParamSecs() {
        return paramSecs;
    }

    public void setRoles(List<EopRoleBean> roles) {
        this.roles = roles;
    }

    public List<EopRoleBean> getRoles() {
        return roles;
    }

}

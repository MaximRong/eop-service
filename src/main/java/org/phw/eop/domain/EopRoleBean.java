package org.phw.eop.domain;

import java.io.Serializable;
import java.util.Date;

public class EopRoleBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String roleid;
    private Date effective, expired;

    @Override
    public String toString() {
        return "EopRoleBean [roleid=" + roleid + ", effective=" + effective + ", expired=" + expired
                + "]";
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

    public String getRoleid() {
        return roleid;
    }

    public Date getEffective() {
        return effective;
    }

    public Date getExpired() {
        return expired;
    }

    public void setRoleid(String roleid) {
        this.roleid = roleid;
    }

    public void setEffective(Date effective) {
        this.effective = effective;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }

}

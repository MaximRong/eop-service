package org.phw.eop.domain;

import java.io.Serializable;
import java.util.List;

import org.phw.eop.support.EopActionSupport;
import org.phw.eop.utils.Clazz;
import org.phw.eop.utils.Strings;

public class EopActionBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String actionid, actionname, actionclass;
    private List<EopActionParamBean> params;
    private List<EopRoleBean> roles;
    private Class<? extends EopActionSupport> actionBeanClass;

    @Override
    public String toString() {
        return "EopActionBean [actionid=" + actionid + ", actionname=" + actionname + ", actionclass="
                + actionclass + ", params=" + params + ", roles=" + roles + "]";
    }

    public void afterPropertiesSet() {
        for (EopActionParamBean param : params) {
            param.afterPropertiesSet();
        }

        if (!Strings.isEmpty(actionclass)) {
            if (!Clazz.classExists(actionclass)) {
                return;
            }

            Class<?> clz = Clazz.forClass(actionclass);
            if (!EopActionSupport.class.isAssignableFrom(clz)) {
                return;
            }
            actionBeanClass = (Class<? extends EopActionSupport>) clz;
        }

    }

    public void setActionclass(String actionclass) {
        this.actionclass = actionclass;
    }

    public String getActionclass() {
        return actionclass;
    }

    public void setActionid(String actionid) {
        this.actionid = actionid;
    }

    public String getActionid() {
        return actionid;
    }

    public void setActionname(String actionname) {
        this.actionname = actionname;
    }

    public String getActionname() {
        return actionname;
    }

    public void setParams(List<EopActionParamBean> params) {
        this.params = params;
    }

    public List<EopActionParamBean> getParams() {
        return params;
    }

    public void setRoles(List<EopRoleBean> roles) {
        this.roles = roles;
    }

    public List<EopRoleBean> getRoles() {
        return roles;
    }

    public EopActionSupport getActionBean() {
        try {
            return Clazz.newInstance(actionBeanClass);
        }
        catch (Exception e) {
            return null;
        }
    }

    public Class<? extends EopActionSupport> getActionBeanClass() {
        return actionBeanClass;
    }
}

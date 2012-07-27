package org.phw.eop.domain;

import java.io.Serializable;

public class EopSystemParam implements Serializable {
    private static final long serialVersionUID = 1L;
    private EopAppBean eopApp;
    private EopActionBean eopAction;
    private EopLogBean eopLog;

    public EopAppBean getEopApp() {
        return eopApp;
    }

    public EopActionBean getEopAction() {
        return eopAction;
    }

    public EopLogBean getEopLog() {
        return eopLog;
    }

    public void setEopApp(EopAppBean eopApp) {
        this.eopApp = eopApp;
    }

    public void setEopAction(EopActionBean eopAction) {
        this.eopAction = eopAction;
    }

    public void setEopLog(EopLogBean eopLog) {
        this.eopLog = eopLog;
    }

}

package org.phw.eop.srv;

import java.util.List;
import java.util.Map;

import org.phw.eop.domain.EopActionBean;
import org.phw.eop.domain.EopAppBean;
import org.phw.eop.domain.EopLogBean;
import org.phw.eop.domain.EopMockBean;
import org.phw.eop.domain.EopSystemParam;
import org.phw.eop.mgr.EopActionMgr;
import org.phw.eop.mgr.EopAppMgr;
import org.phw.eop.mgr.EopLogMgr;
import org.phw.eop.mgr.EopMockMgr;
import org.phw.eop.mgr.EopParamMgr;
import org.phw.eop.mgr.EopStatMgr;
import org.phw.eop.support.EopActionException;
import org.phw.eop.support.EopActionSupport;
import org.phw.eop.support.EopSystemParamAware;

/**
 * SERVICE层的EOP管理类.
 *
 * @author wanglei
 *
 * 2012-6-6
 */
public class EopMgrSrv {

    public EopAppBean getAppByCode(String appCode) {
        return EopAppMgr.getAppByCode(appCode);
    }

    public EopActionBean getActionByName(String actionName) {
        return EopActionMgr.getBean(actionName);
    }

    public EopLogBean newLogTrxid(EopLogBean eopLog) {
        EopLogMgr.createLogTrxid(eopLog);
        return eopLog;
    }

    public EopLogBean newLog(EopLogBean eopLog, String rspCode, EopSystemParam eopSystemParam) {
        EopLogMgr.log(eopLog);
        EopStatMgr.stat(rspCode, eopSystemParam);
        return eopLog;
    }

    public EopMockBean matchMockBean(Map<String, Object> rspParams, EopAppBean eopApp, String actionId) {
        EopMockBean mock = matchMockBean(rspParams, EopMockMgr.getBeans(eopApp.getAppid(), actionId));
        if (mock == null) {
            mock = matchMockBean(rspParams, EopMockMgr.getBeans(actionId));
        }
        return mock;
    }

    public int getParamInt(String paramCode, int defaultValue) {
        return EopParamMgr.getInt(paramCode, defaultValue);
    }

    public boolean getParamBool(String paramCode, boolean defaultValue) {
        return EopParamMgr.getBool(paramCode, defaultValue);
    }

    public boolean checkMinInterval(EopLogBean eopLogBean, int actionMinInterval) {
        return EopStatMgr.checkMinInterval(eopLogBean.getReqts(), eopLogBean.getAppid(),
                eopLogBean.getActionid(), actionMinInterval);
    }

    public boolean increaseAppTimes(EopLogBean eopLog, EopAppBean eopApp, int timesLimit) {
        return EopStatMgr.increaseAppTimes(eopLog.getReqts(), eopApp.getAppid(), timesLimit);
    }

    private EopMockBean matchMockBean(Map<String, Object> rspParams, List<EopMockBean> beans) {
        if (beans == null) {
            return null;
        }
        for (EopMockBean bean : beans) {
            if (bean.matches(rspParams)) {
                return bean;
            }
        }
        return null;
    }

    public Object doBizAction(EopActionBean eopAction, EopSystemParam eopSystemParam, Map<String, Object> params)
            throws EopActionException {
        EopActionSupport actionBean = eopAction.getActionBean();
        if (actionBean == null) {
            throw new RuntimeException("action bean is not well-defined");
        }
        if (actionBean instanceof EopSystemParamAware) {
            ((EopSystemParamAware) actionBean).setSystemParam(eopSystemParam);
        }
        return actionBean.doAction(params);

    }

}

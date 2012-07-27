package org.phw.eop.admin;

import org.phw.eop.mgr.EopAppMgr;
import org.phw.eop.support.EopAction;
import org.phw.eop.support.EopActionException;

public class AppQueryAction extends EopAction {

    @Override
    public Object doAction() throws EopActionException {
        String appid = getStr("appid");
        return EopAppMgr.getAppById(appid);
    }

}

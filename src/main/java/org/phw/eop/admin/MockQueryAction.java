package org.phw.eop.admin;

import org.phw.eop.mgr.EopMockMgr;
import org.phw.eop.support.EopAction;
import org.phw.eop.support.EopActionException;
import org.phw.eop.utils.Strings;

public class MockQueryAction extends EopAction {

    @Override
    public Object doAction() throws EopActionException {
        String appid = getStr("appid");
        String actionid = getStr("actionid");

        return Strings.isEmpty(appid) ? EopMockMgr.getBeans(actionid) : EopMockMgr.getBeans(appid, actionid);
    }

}

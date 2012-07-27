package org.phw.eop.admin;

import java.sql.SQLException;

import org.phw.eop.mgr.EopActionMgr;
import org.phw.eop.mgr.EopAppMgr;
import org.phw.eop.mgr.EopJsonSchemaMgr;
import org.phw.eop.mgr.EopMockMgr;
import org.phw.eop.mgr.EopParamMgr;
import org.phw.eop.support.EopAction;
import org.phw.eop.support.EopActionException;
import org.phw.eop.utils.Strings;

public class CacheUpdateAction extends EopAction {

    @Override
    public Object doAction() throws EopActionException {
        String cacheName = getStr("cacheName");
        try {
            if ("app".equals(cacheName)) {
                String appid = getStr("appid");
                if (!Strings.isEmpty(appid)) {
                    EopAppMgr.refreshCache(appid);
                }
                else {
                    EopAppMgr.refreshCache();
                }
            }
            else if ("action".equals(cacheName)) {
                String actionid = getStr("actionid");
                if (!Strings.isEmpty(actionid)) {
                    EopActionMgr.refreshCache(actionid);
                }
                else {
                    EopActionMgr.refreshCache();
                }
            }
            else if ("mock".equals(cacheName)) {
                String appid = getStr("appid");
                String actionid = getStr("actionid");
                if (!Strings.isEmpty(appid) && !Strings.isEmpty(actionid)) {
                    EopMockMgr.refreshCache(appid, actionid);
                }
                else if (!Strings.isEmpty(appid)) {
                    EopMockMgr.refreshCache(appid);
                }
                else {
                    EopMockMgr.refreshCache();
                }
            }
            else if ("param".equals(cacheName)) {
                EopParamMgr.refreshCache();
            }
            else if ("schema".equals(cacheName)) {
                String uri = getStr("uri");
                if (!Strings.isEmpty(uri)) {
                    EopJsonSchemaMgr.refreshCache(uri);
                }
                else {
                    EopJsonSchemaMgr.refreshCache();
                }
            }
        }
        catch (SQLException ex) {
            throw new EopActionException("B001", ex.getMessage());
        }

        return "OK";
    }
}

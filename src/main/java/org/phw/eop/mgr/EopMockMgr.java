package org.phw.eop.mgr;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.phw.eop.domain.EopMockBean;
import org.phw.eop.utils.Dbs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class EopMockMgr {
    // private final static Compilable engine = (Compilable) new ScriptEngineManager().getEngineByName("JavaScript");
    private static ConcurrentHashMap<String, List<EopMockBean>> cache = new ConcurrentHashMap<String, List<EopMockBean>>();
    private static Logger logger = LoggerFactory.getLogger(EopMockMgr.class);
    static {
        try {
            refreshCache();
        }
        catch (Exception e) {
            logger.error("Load EOP Mock Config Exception", e);
        }
    }

    public static void refreshCache() throws SQLException {
        String sql = "SELECT APPID, ACTIONID, PRI, EXPR, RSP FROM EOP_MOCK ORDER BY APPID, ACTIONID, PRI DESC";

        Connection conn = null;
        List<EopMockBean> list = null;
        try {
            conn = ConnManager.getConnection();
            list = Dbs.queryList(conn, sql, EopMockBean.class);
        }
        finally {
            Dbs.closeQuietly(conn);
        }

        Multimap<String, EopMockBean> multiMap = Multimaps.newListMultimap(
                Maps.<String, Collection<EopMockBean>> newHashMap(),
                new Supplier<List<EopMockBean>>() {
                    @Override
                    public List<EopMockBean> get() {
                        return Lists.newArrayList();
                    }
                });

        for (EopMockBean bean : list) {
            multiMap.put(bean.getAppid() + '-' + bean.getActionid(), bean);
        }

        Set<String> keySet = multiMap.keySet();
        for (String key : keySet) {
            List<EopMockBean> collection = (List<EopMockBean>) multiMap.get(key);
            cache.put(key, collection);
        }

        for (String key : cache.keySet()) {
            if (!keySet.contains(key)) {
                cache.remove(key);
            }
        }
    }

    public static void refreshCache(String appId, String actionId) throws SQLException {
        String sql = "SELECT APPID, ACTIONID, PRI, EXPR, RSP FROM EOP_MOCK "
                + "WHERE APPID = ? AND ACTIONID = ? ORDER BY PRI DESC";

        Connection conn = null;
        List<EopMockBean> list = null;
        try {
            conn = ConnManager.getConnection();
            list = Dbs.queryList(conn, sql, EopMockBean.class, appId, actionId);
        }
        finally {
            Dbs.closeQuietly(conn);
        }

        cache.put(appId + '-' + actionId, list);
    }

    /*
    private static boolean compileExpr(EopMockBean bean) {
        try {
            CompiledScript exprScript = engine.compile(bean.getExpr());
            bean.setExprScript(exprScript);
            return true;
        }
        catch (ScriptException e) {
            logger.error("Compile expr failed:" + bean.getExpr(), e);
            return false;
        }
    }
    */

    public static void refreshCache(String appId) throws SQLException {
        String sql = "SELECT APPID, ACTIONID, PRI, EXPR, RSP FROM EOP_MOCK "
                + "WHERE APPID = ?  ORDER BY ACTIONID, PRI DESC";

        Connection conn = null;
        List<EopMockBean> list = null;
        try {
            conn = ConnManager.getConnection();
            list = Dbs.queryList(conn, sql, EopMockBean.class);
        }
        finally {
            Dbs.closeQuietly(conn);
        }

        Multimap<String, EopMockBean> multiMap = Multimaps.newListMultimap(
                Maps.<String, Collection<EopMockBean>> newHashMap(),
                new Supplier<List<EopMockBean>>() {
                    @Override
                    public List<EopMockBean> get() {
                        return Lists.newArrayList();
                    }
                });

        for (EopMockBean bean : list) {
            multiMap.put(bean.getAppid() + '-' + bean.getActionid(), bean);
        }

        Set<String> keySet = multiMap.keySet();
        for (String key : keySet) {
            List<EopMockBean> collection = (List<EopMockBean>) multiMap.get(key);
            cache.put(key, collection);
        }

        for (String key : cache.keySet()) {
            if (!keySet.contains(key) && key.startsWith(appId + '-')) {
                cache.remove(key);
            }
        }
    }

    /**
     * 找绑定Appid的Action的模拟返回。
     * @param appid AppID
     * @param actionid ActionID
     * @return List<EopMockBean>
     */
    public static List<EopMockBean> getBeans(String appid, String actionid) {
        return cache.get(appid + '-' + actionid);
    }

    /**
     * 找Action默认的模拟返回（不绑定Appid）。
     * @param actionid ActionID
     * @return List<EopMockBean>
     */
    public static List<EopMockBean> getBeans(String actionid) {
        return cache.get("0000-" + actionid);
    }
}

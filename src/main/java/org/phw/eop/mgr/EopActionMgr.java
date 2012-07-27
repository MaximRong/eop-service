package org.phw.eop.mgr;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.phw.eop.domain.EopActionBean;
import org.phw.eop.domain.EopActionParamBean;
import org.phw.eop.domain.EopRoleBean;
import org.phw.eop.utils.Dbs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EopActionMgr {
    private static ConcurrentHashMap<String, EopActionBean> cache = new ConcurrentHashMap<String, EopActionBean>();
    private static Logger logger = LoggerFactory.getLogger(EopActionMgr.class);
    static {
        try {
            refreshCache();
        }
        catch (Exception e) {
            logger.error("Load EOP Action Config Refresh Exception", e);
        }
    }

    public static void refreshCache(String actionId) throws SQLException {
        String sql = "SELECT  ACTIONID, ACTIONNAME, ACTIONCLASS FROM EOP_ACTION WHERE ACTIONID = ?";

        Connection conn = null;
        try {
            conn = ConnManager.getConnection();
            List<EopActionBean> list = Dbs.queryList(conn, sql, EopActionBean.class, actionId);

            for (EopActionBean bean : list) {
                updateActionParams(conn, bean);
            }
        }
        finally {
            Dbs.closeQuietly(conn);
        }
    }

    private static void updateActionParams(Connection conn, EopActionBean bean) throws SQLException {
        String paramSql = "SELECT ACTIONID, NAME, TYPE, REMARK, OPTIONAL, ENCRYPTED, "
                + "MINLEN, MAXLEN, RANGE, PATTERN, VALIDATOR FROM EOP_ACTION_PARAM WHERE ACTIONID = ?";

        List<EopActionParamBean> params = Dbs.queryList(conn, paramSql, EopActionParamBean.class, bean.getActionid());
        bean.setParams(params);
        bean.setRoles(getRoles(conn, bean.getActionid()));

        bean.afterPropertiesSet();
        cache.put(bean.getActionname(), bean);
    }

    public static void refreshCache() throws SQLException {
        String sql = "SELECT  ACTIONID, ACTIONNAME, ACTIONCLASS FROM EOP_ACTION";

        Connection conn = null;
        Set<String> keySet = null;
        try {
            conn = ConnManager.getConnection();
            List<EopActionBean> list = Dbs.queryList(conn, sql, EopActionBean.class);

            keySet = new HashSet<String>(list.size());

            for (EopActionBean bean : list) {
                keySet.add(bean.getActionname());

                updateActionParams(conn, bean);
            }
        }
        finally {
            Dbs.closeQuietly(conn);
        }

        for (String key : cache.keySet()) {
            if (!keySet.contains(key)) {
                cache.remove(key);
            }
        }
    }

    private static List<EopRoleBean> getRoles(Connection conn, String actionid) throws SQLException {
        String sql = "SELECT ROLEID, EFFECTIVE, EXPIRED " +
                "FROM EOP_ACTION_ROLE  WHERE ACTIONID = ? " +
                "AND (EFFECTIVE IS NULL OR SYSDATE >= EFFECTIVE) " +
                "AND (EXPIRED IS NULL OR SYSDATE <= EXPIRED)";

        return Dbs.queryList(conn, sql, EopRoleBean.class, actionid);
    }

    public static EopActionBean getBean(String actionName) {
        return cache.get(actionName);
    }

}

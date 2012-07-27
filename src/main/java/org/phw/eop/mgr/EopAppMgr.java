package org.phw.eop.mgr;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.phw.eop.domain.EopAppBean;
import org.phw.eop.domain.EopAppSecurityBean;
import org.phw.eop.domain.EopRoleBean;
import org.phw.eop.sec.SecurityFactory;
import org.phw.eop.utils.Dbs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EopAppMgr {
    private static ConcurrentHashMap<String, EopAppBean> appCodeCache = new ConcurrentHashMap<String, EopAppBean>();
    private static ConcurrentHashMap<String, EopAppBean> appIdCache = new ConcurrentHashMap<String, EopAppBean>();
    private static Logger logger = LoggerFactory.getLogger(EopAppMgr.class);
    static {
        try {
            refreshCache();
        }
        catch (Exception e) {
            logger.error("Load EOP Application Config Exception", e);
        }
    }

    public static void refreshCache(String appid) throws SQLException {
        String sql = "SELECT APPCODE, APPID, REMARK , SIGN, EFFECTIVE, EXPIRED, "
                + "MOCK, FORMAT FROM EOP_APP WHERE APPID = ?";

        Connection conn = null;
        List<EopAppBean> eopAppBeans = null;
        try {
            conn = ConnManager.getConnection();
            eopAppBeans = Dbs.queryList(conn, sql, EopAppBean.class, appid);

            for (EopAppBean eopAppBean : eopAppBeans) {
                List<EopAppSecurityBean> signSecs = getSecurities(conn, appid, "0"); // SIGN
                eopAppBean.setSignSecs(signSecs);

                List<EopAppSecurityBean> paraSecs = getSecurities(conn, appid, "1"); // PARAM
                eopAppBean.setParamSecs(paraSecs);

                eopAppBean.setRoles(getRoles(conn, appid));

                appCodeCache.put(eopAppBean.getAppcode(), eopAppBean);
                appIdCache.put(eopAppBean.getAppid(), eopAppBean);
            }
        }
        finally {
            Dbs.closeQuietly(conn);
        }
    }

    public static void refreshCache() throws SQLException {
        String sql = "SELECT APPCODE, APPID, REMARK , SIGN, EFFECTIVE, EXPIRED, "
                + "MOCK, FORMAT FROM EOP_APP";

        Connection conn = null;
        List<EopAppBean> eopAppBeans = null;
        Set<String> appCodeSet = null;
        Set<String> appIdSet = null;
        try {
            conn = ConnManager.getConnection();
            eopAppBeans = Dbs.queryList(conn, sql, EopAppBean.class);

            appCodeSet = new HashSet<String>(eopAppBeans.size());
            appIdSet = new HashSet<String>(eopAppBeans.size());

            for (EopAppBean eopAppBean : eopAppBeans) {
                final String appid = eopAppBean.getAppid();
                List<EopAppSecurityBean> signSecs = getSecurities(conn, appid, "0"); // SIGN
                eopAppBean.setSignSecs(signSecs);

                List<EopAppSecurityBean> paraSecs = getSecurities(conn, appid, "1"); // PARAM
                eopAppBean.setParamSecs(paraSecs);

                eopAppBean.setRoles(getRoles(conn, appid));

                appCodeSet.add(eopAppBean.getAppcode());
                appIdSet.add(eopAppBean.getAppid());
                appCodeCache.put(eopAppBean.getAppcode(), eopAppBean);
                appIdCache.put(eopAppBean.getAppid(), eopAppBean);
            }
        }
        finally {
            Dbs.closeQuietly(conn);
        }

        removeUnsedEntries(appCodeSet, appCodeCache);
        removeUnsedEntries(appIdSet, appIdCache);
    }

    private static void removeUnsedEntries(Set<String> usedKeys, ConcurrentHashMap<String, EopAppBean> map) {
        for (String key : map.keySet()) {
            if (!usedKeys.contains(key)) {
                map.remove(key);
            }
        }
    }

    private static List<EopAppSecurityBean> getSecurities(Connection conn, String appid, String type)
            throws SQLException {
        String sql = "SELECT APPID, TYPE, ALGORITHM, VER, EFFECTIVE, EXPIRED, PUBKEY, PRIKEY, REMARK " +
                "FROM EOP_APP_SECURITY  WHERE APPID = ? AND TYPE = ? " +
                "AND (EFFECTIVE IS NULL OR SYSDATE >= EFFECTIVE) " +
                "AND (EXPIRED IS NULL OR SYSDATE <= EXPIRED) ORDER BY VER";

        List<EopAppSecurityBean> secList = Dbs.queryList(conn, sql, EopAppSecurityBean.class, appid, type);
        for (EopAppSecurityBean bean : secList) {
            bean.setSecurityBean(SecurityFactory.getSecurityBean(bean));
        }

        return secList;
    }

    private static List<EopRoleBean> getRoles(Connection conn, String appid) throws SQLException {
        String sql = "SELECT ROLEID, EFFECTIVE, EXPIRED " +
                "FROM EOP_APP_ROLE  WHERE APPID = ? " +
                "AND (EFFECTIVE IS NULL OR SYSDATE >= EFFECTIVE) " +
                "AND (EXPIRED IS NULL OR SYSDATE <= EXPIRED)";

        return Dbs.queryList(conn, sql, EopRoleBean.class, appid);
    }

    public static EopAppBean getAppByCode(String appCode) {
        return appCodeCache.get(appCode);
    }

    public static EopAppBean getAppById(String appId) {
        return appIdCache.get(appId);
    }
}

package org.phw.eop.mgr;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.phw.eop.domain.EopParamBean;
import org.phw.eop.utils.Dbs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EopParamMgr {
    private static ConcurrentHashMap<String, EopParamBean> appCache = new ConcurrentHashMap<String, EopParamBean>();
    private static Logger logger = LoggerFactory.getLogger(EopParamMgr.class);
    static {
        try {
            refreshCache();
        }
        catch (Exception e) {
            logger.error("Load EOP Params Config Exception", e);
        }
    }

    public static void refreshCache() throws SQLException {
        String sql = "SELECT PARAMCODE, PARAMNAME, PARAMVALUE, REMARK FROM EOP_PARAM";

        Connection conn = ConnManager.getConnection();
        List<EopParamBean> beans = null;
        try {
            beans = Dbs.queryList(conn, sql, EopParamBean.class);
        }
        finally {
            Dbs.closeQuietly(conn);
        }
        Set<String> appCodeSet = new HashSet<String>(beans.size());
        for (EopParamBean bean : beans) {
            appCodeSet.add(bean.getParamcode());
            appCache.put(bean.getParamcode(), bean);
        }

        for (String key : appCache.keySet()) {
            if (!appCodeSet.contains(key)) {
                appCache.remove(key);
            }
        }
    }

    public static boolean getBool(String paramCode, boolean defaultValue) {
        EopParamBean eopParamBean = appCache.get(paramCode);
        if (eopParamBean == null) {
            return defaultValue;
        }

        Boolean boolValue = eopParamBean.valueOf(Boolean.class);
        return boolValue != null ? boolValue.booleanValue() : defaultValue;
    }

    public static int getInt(String paramCode, int defaultValue) {
        EopParamBean eopParamBean = appCache.get(paramCode);
        if (eopParamBean == null) {
            return defaultValue;
        }

        Integer intValue = eopParamBean.valueOf(Integer.class);
        return intValue != null ? intValue.intValue() : defaultValue;
    }

    public static String getStr(String paramCode, String defaultValue) {
        EopParamBean eopParamBean = appCache.get(paramCode);
        if (eopParamBean == null) {
            return defaultValue;
        }

        return eopParamBean.getParamvalue();
    }

    public static EopParamBean getParam(String paramCode) {
        return appCache.get(paramCode);
    }
}

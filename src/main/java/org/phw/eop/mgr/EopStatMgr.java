package org.phw.eop.mgr;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.phw.eop.domain.EopAppBean;
import org.phw.eop.domain.EopLogBean;
import org.phw.eop.domain.EopSystemParam;
import org.phw.eop.utils.Dbs;
import org.phw.eop.utils.EopConst;
import org.phw.eop.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EopStatMgr {
    private static Logger logger = LoggerFactory.getLogger(EopStatMgr.class);

    private static void createStatRecord(Connection conn, long reqTs, long lastUpdate, String metric, String metricType)
            throws SQLException {
        String day = EopConst.FMT_YYYYMMDD.format(new Date(reqTs));

        String sql = "MERGE INTO EOP_STAT USING DUAL ON (METRIC = ? AND METRICTYPE = ? AND STATDAY = ?) "
                + "WHEN NOT MATCHED THEN INSERT (METRIC, METRICTYPE, STATDAY, STATNUM, COSTTIME, LASTUPDATE) "
                + "VALUES(?, ?, ?, 0, 0, ?)";
        Dbs.update(conn, sql, metric, metricType, day, metric, metricType, day, new Timestamp(lastUpdate));
    }

    public static boolean increaseAppTimes(long reqTs, String appId, int maxTimes) {
        String day = EopConst.FMT_YYYYMMDD.format(new Date(reqTs));
        String sql = "UPDATE EOP_STAT T  SET T.STATNUM = T.STATNUM + 1 "
                + "WHERE T.STATDAY = ? "
                + "AND T.METRIC = ? AND T.STATNUM < ? "
                + "AND T.METRICTYPE = '1' AND LASTUPDATE = ?";
        Connection conn = null;
        try {
            conn = ConnManager.getConnection();
            createStatRecord(conn, reqTs, 0, appId, "1");

            int effectedRows = Dbs.update(conn, sql, day, appId, maxTimes, new Timestamp(reqTs));
            return effectedRows > 0;
        }
        catch (SQLException e) {
            e.printStackTrace();
            logger.error("update stat error", e);
            return false;
        }
        finally {
            Dbs.closeQuietly(conn);
        }
    }

    public static void updateStatCost(Connection conn, String metric, String metricType, long costTime) {
        String sql = "UPDATE EOP_STAT T  SET T.COSTTIME = T.COSTTIME + ? " +
                "WHERE T.STATDAY = ?  AND T.METRIC = ? AND T.METRICTYPE = ?";
        try {
            Dbs.update(conn, sql, costTime, metric, metricType);
        }
        catch (SQLException e) {
            e.printStackTrace();
            logger.error("update stat error", e);
        }
    }

    private static final String MERGESQL = "MERGE INTO EOP_STAT T USING DUAL ON (T.METRIC=? AND T.METRICTYPE=? AND T.STATDAY=?) "
            + "WHEN MATCHED THEN UPDATE SET T.STATNUM = T.STATNUM + 1, T.COSTTIME = T.COSTTIME + ?, T.LASTUPDATE = ? "
            + "WHEN NOT MATCHED THEN INSERT (METRIC, METRICTYPE, STATDAY, STATNUM, COSTTIME, LASTUPDATE) "
            + "VALUES(?, ?, ?, 1, ?, ?) ";

    private static void executeStat(PreparedStatement ps, long reqTs, String day, String metric, String metricType,
            long costTime)
            throws SQLException {
        int i = 0;
        ps.setString(++i, metric);
        ps.setString(++i, metricType);
        ps.setString(++i, day);
        ps.setLong(++i, costTime);
        ps.setTimestamp(++i, new Timestamp(reqTs));
        ps.setString(++i, metric);
        ps.setString(++i, metricType);
        ps.setString(++i, day);
        ps.setLong(++i, costTime);
        ps.setTimestamp(++i, new Timestamp(reqTs));

        ps.executeUpdate();
    }

    private static void createErrStat(EopLogBean log, String metric, String metricType) {
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = ConnManager.getConnection();
            ps = conn.prepareStatement(MERGESQL);
            final long costTime = log.getRspts() - log.getArrivalts();
            String day = EopConst.FMT_YYYYMMDD.format(new Date(log.getReqts()));
            executeStat(ps, log.getReqts(), day, metric + "-syserr", metricType, costTime);
        }
        catch (SQLException e) {
            e.printStackTrace();
            logger.error("update stat error", e);
        }
        finally {
            Dbs.closeQuietly(conn, ps, null);
        }
    }

    private static void createSuccStat(EopSystemParam eopSystemParam) {
        EopLogBean log = eopSystemParam.getEopLog();
        EopAppBean eopApp = eopSystemParam.getEopApp();
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = ConnManager.getConnection();
            ps = conn.prepareStatement(MERGESQL);
            String day = EopConst.FMT_YYYYMMDD.format(new Date(log.getReqts()));
            final long costTime = log.getRspts() - log.getArrivalts();
            int timesLimit = EopParamMgr.getInt(EopConst.PARAM_PREFIX_APPTIMELIMIT + eopApp.getAppid(), 0);

            if (timesLimit > 0) {
                updateStatCost(conn, log.getAppid(), "1", costTime);
            }
            else {
                executeStat(ps, log.getReqts(), day, log.getAppid(), "1", costTime);
            }
            executeStat(ps, log.getReqts(), day, log.getActionid(), "2", costTime);
            executeStat(ps, log.getReqts(), day, log.getAppid() + '-' + log.getActionid(), "3", costTime);
            executeStat(ps, log.getReqts(), day, log.getAppid() + '-' + log.getClientip(), "4", costTime);
            executeStat(ps, log.getReqts(), day, log.getActionid() + '-' + log.getClientip(), "5", costTime);
            executeStat(ps, log.getReqts(), day, log.getAppid() + '-' + log.getActionid() + '-' + log.getClientip(),
                    "6", costTime);
        }
        catch (SQLException e) {
            e.printStackTrace();
            logger.error("update stat error", e);
        }
        finally {
            Dbs.closeQuietly(conn, ps, null);
        }
    }

    public static void stat(String rspCode, EopSystemParam eopSystemParam) {
        EopLogBean eopLog = eopSystemParam.getEopLog();
        if ("0".equals(rspCode)) {
            EopStatMgr.createSuccStat(eopSystemParam);
        }
        else if (Strings.startsWith(rspCode, "E")) {
            if (Strings.isEmpty(eopLog.getAppid())) {
                createErrStat(eopLog, eopLog.getClientip(), "8");
            }
            else {
                createErrStat(eopLog, eopLog.getAppid() + '-' + eopLog.getClientip(), "7");
            }
        }
    }

    public static boolean checkMinInterval(long reqTs, String appid, String actionid, int appActionMinInterval) {
        String sql = "UPDATE EOP_STAT T "
                + "SET T.LASTUPDATE = ? "
                + "WHERE T.STATDAY = ? "
                + "AND T.METRIC = ? AND T.METRICTYPE = '3' AND  T.LASTUPDATE < ?";
        Connection conn = null;
        try {
            conn = ConnManager.getConnection();
            String metric = appid + '-' + actionid;
            createStatRecord(conn, reqTs, 0, metric, "3");
            String day = EopConst.FMT_YYYYMMDD.format(new Date(reqTs));
            int x = Dbs.update(conn, sql, new Timestamp(reqTs), day,
                    metric, new Timestamp(reqTs - appActionMinInterval * 1000));

            return x > 0;
        }
        catch (SQLException e) {
            e.printStackTrace();
            logger.error("checkMinInterval error", e);
        }
        finally {
            Dbs.closeQuietly(conn);
        }
        return false;
    }
}

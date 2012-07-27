package org.phw.eop.mgr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.phw.eop.domain.EopLogBean;
import org.phw.eop.utils.ByteUtils;
import org.phw.eop.utils.Dbs;
import org.phw.eop.utils.Strings;
import org.phw.eop.utils.ThreadSafeSimpleDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EopLogMgr {
    private static Logger logger = LoggerFactory.getLogger(EopLogMgr.class);

    public static void log(final EopLogBean log) {
        String sql = "INSERT INTO EOP_LOG(TRXID, APPCODE, APPID, ACTIONNAME, ACTIONID, APPTX, REQTS, ARRIVALTS, PATS, AATS, "
                + " CLIENTIP, SERVERIP,RSPTS,RSPCODE,RSPDESC,REQCONTENT,RSPMSG) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Connection conn = null;
        try {
            conn = ConnManager.getConnection();

            Dbs.update(conn, sql, new Dbs.UpdateMapper() {

                @Override
                public void mapping(PreparedStatement ps) throws SQLException {
                    int i = 0;
                    ps.setString(++i, log.getTrxid());
                    ps.setString(++i, Strings.substrb(log.getAppcode(), 32, "UTF-8"));
                    ps.setString(++i, log.getAppid());
                    ps.setString(++i, Strings.substrb(log.getActionname(), 32, "UTF-8"));
                    ps.setString(++i, log.getActionid());
                    ps.setString(++i, Strings.substrb(log.getApptx(), 256, "UTF-8"));
                    ps.setTimestamp(++i, new Timestamp(log.getReqts()));
                    ps.setTimestamp(++i, new Timestamp(log.getArrivalts()));
                    ps.setTimestamp(++i, log.getPats() == 0 ? null : new Timestamp(log.getPats()));
                    ps.setTimestamp(++i, log.getAats() == 0 ? null : new Timestamp(log.getAats()));
                    ps.setString(++i, log.getClientip());
                    ps.setString(++i, log.getServerip());
                    ps.setTimestamp(++i, new Timestamp(log.getRspts()));
                    ps.setString(++i, log.getRspcode());
                    ps.setString(++i, Strings.substrb(log.getRspdesc(), 128, "UTF-8"));
                    ps.setBytes(++i, ByteUtils.toBytes(log.getReqcontent(), "GBK"));
                    ps.setBytes(++i, ByteUtils.toBytes(log.getRspmsg(), "GBK"));
                }
            });
        }
        catch (SQLException e) {
            logger.error("Log EOP error", e);
        }
        finally {
            Dbs.closeQuietly(conn);
        }
    }

    /**
     * yyyy-MM-dd HH:mm:ss.SSS.
     */
    public static final ThreadSafeSimpleDateFormat DAY_FMT = new ThreadSafeSimpleDateFormat(
            "yyyyMMdd");

    private static String fixLength(String s, char c, int fixedLen) {
        if (Strings.isEmpty(s)) {
            return Strings.dup(c, fixedLen);
        }

        int length = s.length();
        if (length > fixedLen) {
            return s.substring(length - fixedLen);
        }

        return new StringBuilder(Strings.dup(c, fixedLen - length)).append(s).toString();
    }

    public static void createLogTrxid(EopLogBean log) {
        String sql = "SELECT EOP_LOG_SEQ.NEXTVAL FROM DUAL";
        Connection conn = null;
        try {
            conn = ConnManager.getConnection();
            String seq = Dbs.query(conn, sql);
            String day = DAY_FMT.format(new Date());

            StringBuilder trxid = new StringBuilder()
                    .append(fixLength(log.getAppid(), '0', 4))
                    .append(fixLength(log.getActionid(), '0', 4))
                    .append(day).append(fixLength(seq, '0', 16));
            log.setTrxid(trxid.toString());
        }
        catch (SQLException e) {
            logger.error("Log EOP error", e);
        }
        finally {
            Dbs.closeQuietly(conn);
        }
    }

}

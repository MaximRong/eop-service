package org.phw.eop.mgr;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.phw.eop.domain.EopJsonSchemaBean;
import org.phw.eop.utils.Dbs;
import org.phw.jsv.JSVEnv;
import org.phw.jsv.JSVException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EopJsonSchemaMgr {
    private static ReadWriteLock lock = new ReentrantReadWriteLock();
    private static volatile JSVEnv jsvEnv;
    private static Logger logger = LoggerFactory.getLogger(EopJsonSchemaMgr.class);
    static {
        try {
            jsvEnv = new JSVEnv();
            refreshCache();
        }
        catch (Exception e) {
            logger.error("Load Json Schemas Exception", e);
        }
    }

    public static void refreshCache() throws SQLException {
        String sql = "SELECT URI, SEQ, SCHEMA, REMARK FROM EOP_JSONSCHEMA ORDER BY SEQ";
        Connection conn = null;
        try {
            lock.writeLock().lock();
            conn = ConnManager.getConnection();
            List<EopJsonSchemaBean> beans = Dbs.queryList(conn, sql, EopJsonSchemaBean.class);
            for (EopJsonSchemaBean bean : beans) {
                try {
                    jsvEnv.createSchema(bean.getSchema(), bean.getUri());
                }
                catch (JSVException e) {
                    logger.error("Load Json Schemas " + bean.getUri() + " Exception", e);
                }
            }
        }
        finally {
            lock.writeLock().unlock();
            Dbs.closeQuietly(conn);
        }
    }

    public static void refreshCache(String uri) throws SQLException {
        String sql = "SELECT URI, SEQ, SCHEMA, REMARK FROM EOP_JSONSCHEMA ORDER BY SEQ WHERE URI  = ?";
        Connection conn = null;
        try {
            lock.writeLock().lock();
            conn = ConnManager.getConnection();
            EopJsonSchemaBean bean = Dbs.query(conn, sql, EopJsonSchemaBean.class, uri);
            try {
                jsvEnv.createSchema(bean.getRemark(), bean.getUri());
            }
            catch (JSVException e) {
                logger.error("Load Json Schemas " + bean.getUri() + " Exception", e);
            }
        }
        finally {
            lock.writeLock().unlock();
            Dbs.closeQuietly(conn);
        }
    }

    public static String validateJson(String json, String schemaUri) {
        try {
            lock.readLock().lock();
            return jsvEnv.validateJson(json, schemaUri);
        }
        finally {
            lock.readLock().unlock();
        }
    }
}

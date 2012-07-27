package org.phw.eop.mgr;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.phw.eop.utils.sec.CrypterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;

public class ConnManager {
    private static ComboPooledDataSource ds     = null;
    private static Logger                logger = LoggerFactory.getLogger(ConnManager.class);

    static {
        try {
            Properties p = new Properties();
            final InputStream envspaceIn = ConnManager.class.getResourceAsStream("/envspace.props");
            p.load(envspaceIn);
            String envspacedir = p.getProperty("envspacedir");
            final InputStream is = ConnManager.class.getResourceAsStream("/" + envspacedir + "/eop/c3p0.properties");
            p.load(is);
            ds = new ComboPooledDataSource();
            ds.setUser(p.getProperty("user"));
            ds.setPassword(decryptPassword(p.getProperty("password")));
            ds.setJdbcUrl(p.getProperty("jdbcUrl"));
            ds.setDriverClass(p.getProperty("driverClass"));
            ds.setInitialPoolSize(Integer.parseInt(p.getProperty("initialPoolSize")));
            ds.setMinPoolSize(Integer.parseInt(p.getProperty("minPoolSize")));
            ds.setMaxPoolSize(Integer.parseInt(p.getProperty("maxPoolSize")));
            ds.setMaxIdleTime(Integer.parseInt(p.getProperty("maxIdleTime")));
            ds.setAutoCommitOnClose(Boolean.parseBoolean(p.getProperty("autoCommitOnClose")));
        } catch (Exception e) {
            logger.error("C3PO error", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();

    }

    @Override
    protected void finalize() throws Throwable {
        DataSources.destroy(ds); // 关闭datasource
        super.finalize();
    }

    /**
     * jdbc password解密。
     * 
     * @param password
     * @return
     * @throws IOException
     */
    private static String decryptPassword(String password) throws IOException {
        Properties p = new Properties();
        final InputStream envspaceIn = ConnManager.class.getResourceAsStream("/envspace.props");
        p.load(envspaceIn);
        String envspacedir = p.getProperty("envspacedir");
        final InputStream is = ConnManager.class.getResourceAsStream("/" + envspacedir + "/eop/eopenv.properties");
        p.load(is);
        return CrypterUtils.decrypt(p.getProperty("DbPwdKey"), password);
    }
}

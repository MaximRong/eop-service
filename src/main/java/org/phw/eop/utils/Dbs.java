package org.phw.eop.utils;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A collection of JDBC helper methods.  This class is thread safe.
 * Most of code are from apache commons-dbutils.
 * @author BingooHuang
 *
 */
public class Dbs {
    public static interface ResultMapper<T> {
        T mapping(ResultSet rs) throws SQLException;
    }

    public static interface UpdateMapper {
        void mapping(PreparedStatement ps) throws SQLException;
    }

    /**
     * Close a <code>Connection</code>, avoid closing if null and hide
     * any SQLExceptions that occur.
     *
     * @param conn Connection to close.
     */
    public static void closeQuietly(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e) {
            // quiet
        }
    }

    /**
     * Close a <code>Connection</code>, <code>Statement</code> and 
     * <code>ResultSet</code>.  Avoid closing if null and hide any 
     * SQLExceptions that occur.
     *
     * @param conn Connection to close.
     * @param stmt Statement to close.
     * @param rs ResultSet to close.
     */
    public static void closeQuietly(Connection conn, Statement stmt, ResultSet rs) {

        try {
            closeQuietly(rs);
        }
        finally {
            try {
                closeQuietly(stmt);
            }
            finally {
                closeQuietly(conn);
            }
        }

    }

    /**
     * Close a <code>ResultSet</code>, avoid closing if null and hide any
     * SQLExceptions that occur.
     *
     * @param rs ResultSet to close.
     */
    public static void closeQuietly(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            // quiet
        }
    }

    /**
     * Close a <code>Statement</code>, avoid closing if null and hide
     * any SQLExceptions that occur.
     *
     * @param stmt Statement to close.
     */
    public static void closeQuietly(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        }
        catch (SQLException e) {
            // quiet
        }
    }

    /**
     * Loads and registers a database driver class.
     * If this succeeds, it returns true, else it returns false.
     *
     * @param driverClassName of driver to load
     * @return boolean <code>true</code> if the driver was found, otherwise <code>false</code>
     */
    public static boolean loadDriver(String driverClassName) {
        try {
            Class.forName(driverClassName).newInstance();
            return true;

        }
        catch (ClassNotFoundException e) {
            return false;

        }
        catch (IllegalAccessException e) {
            // Constructor is private, OK for DriverManager contract
            return true;

        }
        catch (InstantiationException e) {
            return false;

        }
        catch (Throwable e) {
            return false;
        }
    }

    private static class AutoRowMapper<T> implements ResultMapper<T> {
        private final List<FieldInfo> computeSetters;
        private Class<T> retClass;

        public AutoRowMapper(Class<T> retClass) {
            computeSetters = Reflects.computeSetters(retClass);
            this.retClass = retClass;
        }

        @Override
        public T mapping(ResultSet rs) throws SQLException {
            final T ret = Clazz.newInstance(retClass);
            ResultSetMetaData metaData = rs.getMetaData();
            Set<String> fieldNames = new HashSet<String>(metaData.getColumnCount());
            for (int i = 1; i <= metaData.getColumnCount(); ++i) {
                fieldNames.add(metaData.getColumnName(i).toUpperCase());
            }

            for (FieldInfo fieldInfo : computeSetters) {
                try {
                    if (!fieldNames.contains(fieldInfo.getName().toUpperCase())) {
                        continue;
                    }

                    if (fieldInfo.getFieldClass() == String.class) {
                        String str = rs.getString(fieldInfo.getName());
                        fieldInfo.set(ret, str);
                        continue;
                    }

                    Object fieldValue = rs.getObject(fieldInfo.getName());
                    if (fieldValue == null) {
                        continue;
                    }

                    if (fieldValue instanceof String) {
                        fieldValue = ((String) fieldValue).trim();

                        if (fieldInfo.getFieldClass() == boolean.class
                                || fieldInfo.getFieldType() == Boolean.class) {
                            fieldValue = new Boolean("Y".equalsIgnoreCase((String) fieldValue)
                                           || "1".equals(fieldValue));
                        }
                        fieldInfo.set(ret, fieldValue);
                    }
                    else if (fieldValue instanceof Number) {
                        fieldInfo.set(ret, Converts.convert("" + fieldValue, fieldInfo.getFieldClass()));
                    }
                    else if (fieldValue instanceof java.sql.Date || fieldValue instanceof Timestamp) {
                        if (fieldInfo.getFieldClass() == Timestamp.class) {
                            fieldInfo.set(ret, rs.getTimestamp(fieldInfo.getName()));
                        }
                        else if (fieldInfo.getFieldClass() == Date.class) {
                            fieldInfo.set(ret, rs.getDate(fieldInfo.getName()));
                        }
                        else if (fieldInfo.getFieldClass() == long.class || fieldInfo.getFieldClass() == Long.class) {
                            Timestamp timestamp = rs.getTimestamp(fieldInfo.getName());
                            fieldInfo.set(ret, timestamp.getTime());
                        }
                    }
                    else {
                        fieldValue = rs.getString(fieldInfo.getName());
                        fieldInfo.set(ret, Converts.convert((String) fieldValue, fieldInfo.getFieldClass()));
                    }
                }
                catch (SQLException ex) {
                }
                catch (IllegalAccessException e) {
                }
                catch (InvocationTargetException e) {
                }

            }
            return ret;
        }
    }

    public static String query(Connection conn, String sql, Object... params) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; ++i) {
                ps.setObject(i + 1, params[i]);
            }
            rs = ps.executeQuery();
            return rs.next() ? rs.getString(1) : null;
        }
        finally {
            closeQuietly(null, ps, rs);
        }
    }

    public static <T> T query(Connection conn, String sql, Class<T> retClass, Object... params) throws SQLException {
        return query(conn, sql, new AutoRowMapper<T>(retClass), params);
    }

    public static <T> List<T> queryList(Connection conn, String sql, Class<T> retClass, Object... params)
            throws SQLException {
        return queryList(conn, sql, new AutoRowMapper<T>(retClass), params);
    }

    public static <T> List<T> queryList(Connection conn, String sql, ResultMapper<T> rowMapper, Object... params)
            throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; ++i) {
                ps.setObject(i + 1, params[i]);
            }
            rs = ps.executeQuery();
            ArrayList<T> arrayList = new ArrayList<T>();
            while (rs.next()) {
                arrayList.add(rowMapper.mapping(rs));
            }

            return arrayList;
        }
        finally {
            closeQuietly(null, ps, rs);
        }
    }

    public static <T> T query(Connection conn, String sql, ResultMapper<T> rowMapper, Object... params)
            throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; ++i) {
                ps.setObject(i + 1, params[i]);
            }
            rs = ps.executeQuery();
            return rs.next() ? rowMapper.mapping(rs) : null;
        }
        finally {
            closeQuietly(null, ps, rs);
        }
    }

    public static Connection getConn(String driver, String dbURL, String user, String pass) throws SQLException,
            ClassNotFoundException {
        Class.forName(driver);
        return DriverManager.getConnection(dbURL, user, pass);
    }

    public static int update(Connection conn, String sql, UpdateMapper mapper) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            mapper.mapping(ps);
            return ps.executeUpdate();
        }
        finally {
            closeQuietly(ps);
        }
    }

    public static int update(Connection conn, String sql, Object... params) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; ++i) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeUpdate();
        }
        finally {
            closeQuietly(ps);
        }
    }

}

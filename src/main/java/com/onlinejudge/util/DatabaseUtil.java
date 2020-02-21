package com.onlinejudge.util;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUtil {
    private static DataSource ds = new ComboPooledDataSource();
    private static ThreadLocal<Connection> tl = new ThreadLocal<>();

    public DatabaseUtil() {}

    public static DataSource getDataSource() {
        return ds;
    }

    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = tl.get();
            if (conn == null) {
                conn = ds.getConnection();
                tl.set(conn);
            }
            return conn;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void closeConnection() {
        try {
            Connection conn = tl.get();
            if (conn != null) conn.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            tl.remove();
        }
    }

    public static PreparedStatement prepareStatement(String cmd) throws SQLException {
        assert (tl.get() != null);
        return tl.get().prepareStatement(cmd);
    }

    public static void closeQuery(ResultSet resultSet, PreparedStatement preparedStatement, Connection connection) throws SQLException {
        if (resultSet != null) resultSet.close();
        if (preparedStatement != null) preparedStatement.close();
        if (connection != null) closeConnection();
    }

    public static void closeUpdate(PreparedStatement preparedStatement, Connection connection) throws SQLException {
        if (preparedStatement != null) preparedStatement.close();
        if (connection != null) closeConnection();
    }
}

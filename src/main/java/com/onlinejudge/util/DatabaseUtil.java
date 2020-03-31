package com.onlinejudge.util;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.jetbrains.annotations.Contract;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final DataSource ds = new ComboPooledDataSource();
    private static final ThreadLocal<Connection> tl = new ThreadLocal<>();

    @Contract(pure = true)
    private DatabaseUtil() {
    }

    @Contract(pure = true)
    public static DataSource getDataSource() {
        return ds;
    }

    public static Connection getConnection() {
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
        return getConnection().prepareStatement(cmd);
    }

    public static void closeQuery(ResultSet resultSet, PreparedStatement preparedStatement, Connection connection) throws SQLException {
        if (resultSet != null) resultSet.close();
        if (preparedStatement != null) preparedStatement.close();
        if (connection != null) closeConnection();
    }

    public static void closeQuery(ResultSet resultSet, PreparedStatement preparedStatement) throws SQLException {
        if (resultSet != null) resultSet.close();
        if (preparedStatement != null) preparedStatement.close();
        closeConnection();
    }

    public static void closeUpdate(PreparedStatement stmt) throws SQLException {
        if (stmt != null) stmt.close();
        closeConnection();
    }

    public static void closeUpdate(PreparedStatement preparedStatement, Connection connection) throws SQLException {
        if (preparedStatement != null) preparedStatement.close();
        if (connection != null) closeConnection();
    }
}

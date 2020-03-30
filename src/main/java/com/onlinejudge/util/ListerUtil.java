package com.onlinejudge.util;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.onlinejudge.util.DatabaseUtil.*;
@SuppressWarnings("unchecked")
public class ListerUtil<T> {
    private ResultSet result = null;
    private static final Logger logger = LoggerFactory.getLogger(ListerUtil.class);
    private Connection conn = null;
    private PreparedStatement stmt = null;
    private List<T> resultList;

    public ListerUtil(String stmtString, String get, @NotNull List<String> puts, String callName) {
        this.resultList = new ArrayList<>();
        try {
            this.conn = getConnection();
            this.stmt = prepareStatement(stmtString);
            int cur = 0;
            for (String it : puts) {
                this.stmt.setString(++cur, it);
            }
            logger.debug("{},{}", callName, this.stmt);
            this.result = this.stmt.executeQuery();
            while (this.result.next()) this.resultList.add((T) this.result.getObject(get));
            closeQuery(this.result, this.stmt, this.conn);
            logger.info("{}, , query is ok, quit.", callName);
        } catch (SQLException e) {
            try {
                closeQuery(this.result, this.stmt, this.conn);
            } catch (SQLException ex) {
                logger.error(ex.getMessage(),ex);
            }
        }
    }

    public ListerUtil(String stmtString, String get, String put, String callName) {
        this.resultList = new ArrayList<>();
        try {
            this.conn = getConnection();
            this.stmt = prepareStatement(stmtString);
            this.stmt.setString(1, put);
            logger.debug("{},{}", callName, this.stmt);
            this.result = this.stmt.executeQuery();
            while (this.result.next()) this.resultList.add((T) this.result.getObject(get));
            closeQuery(this.result, this.stmt, this.conn);
            logger.info("{},query is ok, quit.", callName);
        } catch (SQLException e) {
            try {
                closeQuery(this.result, this.stmt, this.conn);
            } catch (SQLException ex) {
                logger.error("SQLException while closing conn", ex);
            }
        }
    }

    public ListerUtil(String stmtString, String get, String callName) {
        this.resultList = new ArrayList<>();
        try {
            this.conn = getConnection();
            this.stmt = prepareStatement(stmtString);
            logger.debug("{},{}", callName, this.stmt);
            this.result = this.stmt.executeQuery();
            while (this.result.next()) this.resultList.add((T) this.result.getObject(get));
            closeQuery(this.result, this.stmt, this.conn);
            logger.info("{},query is ok, quit.", callName);
        } catch (SQLException e) {
            try {
                closeQuery(this.result, this.stmt, this.conn);
            } catch (SQLException ex) {
                logger.error("SQLException while closing conn", ex);
            }
        }
    }

    public List<T> getResultList() {
        return this.resultList;
    }
}

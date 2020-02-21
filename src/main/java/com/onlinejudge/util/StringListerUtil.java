package com.onlinejudge.util;

import com.onlinejudge.DaemonService.DaemonServiceMain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.onlinejudge.util.DatabaseUtil.*;

public class StringListerUtil {
    private ResultSet result = null;
    private List<String> resultList = null;
    private Connection conn = null;
    private PreparedStatement stmt = null;

    public StringListerUtil(String stmtString, String get, List<String> puts, String callName) {
        this.resultList = new ArrayList<>();
        try {
            this.conn = getConnection();
            this.stmt = prepareStatement(stmtString);
            int cur = 0;
            for (String it : puts) {
                this.stmt.setString(++cur, it);
            }
            DaemonServiceMain.debugPrint(callName + " , " + this.stmt.toString());
            this.result = this.stmt.executeQuery();
            while (this.result.next()) this.resultList.add(this.result.getString(get));
            closeQuery(this.result, this.stmt, this.conn);
            DaemonServiceMain.debugPrint(callName + " , query is ok, quit.");
        } catch (SQLException e) {
            try {
                closeQuery(this.result, this.stmt, this.conn);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public StringListerUtil(String stmtString, String get, String put, String callName) {
        this.resultList = new ArrayList<>();
        try {
            this.conn = getConnection();
            this.stmt = prepareStatement(stmtString);
            this.stmt.setString(1, put);
            DaemonServiceMain.debugPrint(callName + " , " + this.stmt.toString());
            this.result = this.stmt.executeQuery();
            while (this.result.next()) this.resultList.add(this.result.getString(get));
            closeQuery(this.result, this.stmt, this.conn);
            DaemonServiceMain.debugPrint(callName + " , query is ok, quit.");
        } catch (SQLException e) {
            try {
                closeQuery(this.result, this.stmt, this.conn);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public List<String> getResultList() {
        return this.resultList;
    }
}

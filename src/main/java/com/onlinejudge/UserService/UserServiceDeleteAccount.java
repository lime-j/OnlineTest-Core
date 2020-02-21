package com.onlinejudge.UserService;

import com.onlinejudge.util.BooleanEvent;
import com.onlinejudge.util.DatabaseUtil;

import java.sql.Connection;

import static com.onlinejudge.DaemonService.DaemonServiceMain.debugPrint;

public class UserServiceDeleteAccount extends BooleanEvent {


    private String userID;

    public UserServiceDeleteAccount(String userID) {
        this.userID = userID;
    }

    public boolean go() {
        Connection conn;
        try {
            //    Class.forName(JDBC_DRIVER);
            conn = DatabaseUtil.getConnection();
            debugPrint("UserServiceDeleteAccount, conn to DB");
            //stmt.executeQuery("use onlinejudge");
            var stmt = DatabaseUtil.prepareStatement("delete from userinfo where uid = ?");
            stmt.setString(1, this.userID);
            debugPrint("UserServiceDeleteAccount, " + stmt.toString());
            stmt.executeUpdate();
            DatabaseUtil.closeUpdate(stmt, conn);
            debugPrint("UserServiceDeleteAccount, query is ok, quit.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

package com.onlinejudge.userservice;

import com.onlinejudge.util.BooleanEvent;
import com.onlinejudge.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserServiceDeleteAccount extends BooleanEvent {
    private static Logger logger = LoggerFactory.getLogger(UserServiceDeleteAccount.class);

    private String userID;

    public UserServiceDeleteAccount(String userID) {
        this.userID = userID;
    }

    public boolean go() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            //    Class.forName(JDBC_DRIVER);
            conn = DatabaseUtil.getConnection();
            logger.info("conn to DB");
            /* stmt.executeQuery("use onlinejudge"); */
            stmt = DatabaseUtil.prepareStatement("delete from userinfo where uid = ?");
            stmt.setString(1, this.userID);
            logger.debug(stmt.toString());
            stmt.executeUpdate();
            DatabaseUtil.closeUpdate(stmt, conn);
            logger.info("query is ok, quit.");
            return true;
        } catch (SQLException e) {
            logger.error("SQLException",e);

        }finally {
            try {
                DatabaseUtil.closeUpdate(stmt, conn);
            } catch (SQLException e) {
                logger.error("SQLException while closing",e);
            }
        }
        return false;
    }
}

package com.onlinejudge.manservice;

import com.onlinejudge.util.DatabaseUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.onlinejudge.util.DatabaseUtil.*;

public class UserWithPasswd extends com.onlinejudge.userservice.User {
    private String UserPasswd;
    private static Logger logger = LoggerFactory.getLogger(UserWithPasswd.class);
/*
    不再需要Tuid
    public UserWithPasswd(String userID, String userName, int userSex, int userType, String Tuid, String Passwd) {
        super(userID, userName, userSex, userType, Tuid);
        this.UserPasswd = Passwd;
    }
*/
    public UserWithPasswd(String userID, String userName, int userSex, int userType, String Passwd) {
        super(userID, userName, userSex, userType);
        this.UserPasswd = Passwd;
    }
    private static boolean isfailed(@NotNull ResultSet rs) {
        boolean flag = true;
        try {
            while (rs.next()) {
                flag = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    public boolean updateUser() {
        // 以下代码来自problem的复制粘贴
        Connection conn = null;
        ResultSet queryResult = null;
        PreparedStatement sta = null;
        try {
            conn = DatabaseUtil.getConnection();
            sta = prepareStatement("select * from userinfo where uid = ?");
            sta.setString(1, super.getUserID());
            queryResult = sta.executeQuery();
            String cmd;
            if (isfailed(queryResult)) {
                // 新用户增加，执行insert
                cmd = String.format("insert into userinfo (uid, uname, utype, upassword, usex)" +
                        "values ('%s', '%s', '%d', '%s', '%d')", super.getUserID(), super.getUserName(), super.getUserType(), this.UserPasswd, super.getUserSex());
                System.out.println(cmd);
            } else {
                // 当前用户已存在，返回false
                return false;
            }
            queryResult.close();
            sta.close();
            sta = prepareStatement("insert into userinfo (uid, uname, utype, upassword, usex) values (?, ?, ?, ?, ?)");
            sta.setString(1, getUserID());
            sta.setString(2, getUserName());
            sta.setInt(3, getUserType());
            sta.setString(4, this.UserPasswd);
            sta.setInt(5, getUserSex());
            logger.debug("UserServiceUpdateUser, " + sta.toString());
            sta.executeUpdate();
            closeUpdate(sta, conn);
            return true;
        } catch (SQLException e) {
            try {
                closeQuery(queryResult, sta, conn);
            } catch (SQLException ex) {
                logger.error(ex.getMessage(), ex);
            }
            logger.error(e.getMessage(),e);
            return false;
        }
    }

}

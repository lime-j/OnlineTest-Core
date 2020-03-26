package com.onlinejudge.userservice;

import com.onlinejudge.util.BooleanEvent;
import com.onlinejudge.util.InternalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.onlinejudge.userservice.UserServiceProperties.*;
import static com.onlinejudge.util.DatabaseUtil.*;

public class UserServiceUpdateProperties extends BooleanEvent {
    private String userID, newProperty;
    private int updateType;
    private static Logger logger = LoggerFactory.getLogger(UserServiceUpdateProperties.class);

    // type == 1, 更新性别, 为了统一, 传入的是String, 但会被prase一下
    // type == 2, 更新昵称,
    // type == 3, 更新密码
    public UserServiceUpdateProperties(String userID, String newProperty, int updateType) {
        this.userID = userID;
        this.newProperty = newProperty;
        this.updateType = updateType;
    }

    public boolean go() throws InternalException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            if (this.updateType == changeUserSex) {
                stmt = prepareStatement("update userinfo set usex = ? where uid = ?");
                stmt.setString(2, this.userID);
                stmt.setInt(1, Integer.parseInt(this.newProperty));
            } else if (this.updateType == changeUserName) {
                stmt = prepareStatement("update userinfo set uname= ? where uid = ?");
                stmt.setString(1, this.newProperty);
                stmt.setString(2, this.userID);
            } else if (this.updateType == changeUserPassword) {
                stmt = prepareStatement("update userinfo set upassword = ? where uid = ?");
                stmt.setString(1, this.newProperty);
                stmt.setString(2, this.userID);
            } else return false;
            logger.info("sql query = {}",stmt);
            stmt.executeUpdate();
            closeUpdate(stmt, conn);
            logger.info("UserServiceUpdateProperties, query is ok, quit.");
            return true;
        } catch (SQLException e) {
            try {
                closeUpdate(stmt, conn);
            } catch (SQLException ex) {
                logger.error("SQLException while closing db", ex);
            }
            logger.error("SQLException",e);
            return false;
        }
    }
}


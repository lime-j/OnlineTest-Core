package com.onlinejudge.userservice;

import com.onlinejudge.util.BooleanEvent;
import com.onlinejudge.util.InternalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.onlinejudge.userservice.UserServiceProperties.changeUserName;
import static com.onlinejudge.userservice.UserServiceProperties.changeUserPassword;
import static com.onlinejudge.userservice.UserServiceProperties.changeUserSex;
import static com.onlinejudge.util.DatabaseUtil.closeUpdate;
import static com.onlinejudge.util.DatabaseUtil.getConnection;
import static com.onlinejudge.util.DatabaseUtil.prepareStatement;

public class UserServiceUpdateProperties implements BooleanEvent {
    private final String userID;
    private final String newProperty;
    private final int updateType;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceUpdateProperties.class);

    // type == 1, 更新性别, 为了统一, 传入的是String, 但会被prase一下
    // type == 2, 更新昵称,
    // type == 3, 更新密码
    public UserServiceUpdateProperties(String userID, String newProperty, int updateType) {
        this.userID = userID;
        this.newProperty = newProperty;
        this.updateType = updateType;
    }

    @Override
    public void beforeGo() {
        // do nothing
    }

    @Override
    public void afterGo() {
        // do nothing
    }

    public boolean go() throws InternalException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            if (this.updateType == changeUserSex.getId()) {
                stmt = prepareStatement("update userinfo set usex = ? where uid = ?");
                stmt.setString(2, this.userID);
                stmt.setInt(1, Integer.parseInt(this.newProperty));
            } else if (this.updateType == changeUserName.getId()) {
                stmt = prepareStatement("update userinfo set uname= ? where uid = ?");
                stmt.setString(1, this.newProperty);
                stmt.setString(2, this.userID);
            } else if (this.updateType == changeUserPassword.getId()) {
                stmt = prepareStatement("update userinfo set upassword = ? where uid = ?");
                stmt.setString(1, this.newProperty);
                stmt.setString(2, this.userID);
            } else return false;
            logger.info("sql query = {}", stmt);
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
            logger.error("SQLException", e);
            return false;
        }
    }
}


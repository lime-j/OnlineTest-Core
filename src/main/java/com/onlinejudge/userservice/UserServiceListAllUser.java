package com.onlinejudge.userservice;


import com.onlinejudge.util.InternalException;
import com.onlinejudge.util.ListEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.onlinejudge.util.DatabaseUtil.*;

public class UserServiceListAllUser extends ListEvent {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceListAllUser.class);
    // This can be only triggered by admin
    // listing
    public UserServiceListAllUser() {}

    public List<User> go() throws InternalException {
        // 建立列表
        List<User> result = new ArrayList<>();
        Connection conn = null;
        ResultSet ret = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = prepareStatement("select uid, uname,usex,utype from userinfo where utype = 3 or utype = 2;");
            // 在数据库里边遍历所有用户
            ret = stmt.executeQuery();
            int cnt = 0;
            while (ret.next()) {
                cnt++;
                String userID = ret.getString("uid");
                String userName = ret.getString("uname");
                int userSex = ret.getInt("usex");
                int userType = ret.getInt("utype");
                result.add(new User(userID, userName, userSex, userType));
            }
            logger.info("Event ListAllUsers Finished, {} user(s) listed",cnt);
            closeQuery(ret, stmt, conn);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            try {
                closeQuery(ret, stmt, conn);
            } catch (SQLException e) {
                logger.error("SQL exception while closing",e);
            }
        }
        return result;
    }
}

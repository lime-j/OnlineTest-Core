package com.onlinejudge.UserService;


import com.onlinejudge.util.ListEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.onlinejudge.DaemonService.DaemonServiceMain.debugPrint;
import static com.onlinejudge.util.DatabaseUtil.*;

public class UserServiceListAllUser extends ListEvent {
    // This can be only triggered by admin
    public UserServiceListAllUser() {
    }

    public List<User> go() {
        // 建立列表
        List<User> result = new ArrayList<User>();
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
                var currentUser = new User(userID, userName, userSex, userType);
                result.add(currentUser);
            }
            debugPrint("Event ListAllUsers Finished, " + cnt + "user(s) listed");
            closeQuery(ret, stmt, conn);
            return result;
        } catch (SQLException e) {
            try {
                closeQuery(ret, stmt, conn);
            } catch (Exception ee) {
                ee.printStackTrace();
            }
            e.printStackTrace();
        }
        return result;
    }
}

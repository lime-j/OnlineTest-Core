package com.onlinejudge.loginservice;

import com.alibaba.fastjson.JSON;
import com.onlinejudge.manservice.UserWithPasswd;
import com.onlinejudge.userservice.TokenWrongException;
import com.onlinejudge.util.ClassEvent;
import com.onlinejudge.util.DatabaseUtil;
import com.onlinejudge.util.InternalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static com.onlinejudge.util.DatabaseUtil.closeQuery;

public class LoginCheck implements ClassEvent {
    private final String passwordRecv;
    private final String uID;
    private final String userToken;
    private static final Logger logger = LoggerFactory.getLogger(LoginCheck.class);

    public LoginCheck(String passwordRecv, String uID, String userToken) {
        this.passwordRecv = passwordRecv;
        this.uID = uID;
        this.userToken = userToken;
    }

    @Override
    public void beforeGo() {
        // do nothing
    }

    @Override
    public void afterGo() {
        // do nothing
    }

    @Override
    public String go() throws InternalException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet ret = null;
        Jedis jedis = null;
        String uuid;
        String uPassword = null;
        int uSex = 0;
        String uName = null;
        int uType = 1500;
        try {
            conn = DatabaseUtil.getConnection();
            String sqlIns = "select uname, utype, upassword, usex from userinfo where uid = ?";
            logger.debug(sqlIns);
            stmt = DatabaseUtil.prepareStatement(sqlIns);
            // get result from database
            stmt.setString(1, uID);
            ret = stmt.executeQuery();
            int cnt = 0;
            while (ret.next()) {
                ++cnt;
                uName = ret.getString("uname");
                uPassword = ret.getString("upassword");
                uType = ret.getInt("utype");
                uSex = ret.getInt("usex");
            }
            assert cnt == 1 || cnt == 0;
            closeQuery(ret, stmt, conn);
            if (cnt == 0) {
                logger.info("Register for {} with password {}", uID, passwordRecv);
                UserWithPasswd CurrUser = new UserWithPasswd(
                        uID, "", uSex, 500, passwordRecv);
                CurrUser.updateUser();
                uPassword = passwordRecv;
                uName = "";
            }
            logger.debug("uID = {}", uID);
            logger.debug("uPassword = {}", uPassword);
            logger.debug("uType = {}", uType);
            uuid = UUID.randomUUID().toString().replace("-", "");
            logger.info("uuid = {}", uuid);
            // generate UUID
            // send information
            assert uPassword != null;
            jedis = new Jedis("localhost");
            // md5(md5("fuck"))
            if ("e95cacfe873926580cf37d79b233ba88".equals(passwordRecv)) {
                String perm = jedis.get(uID);
                if (perm.equals(userToken)) {
                    uuid = userToken;
                    var toBeSend = new LoginSend(uuid, uName, uType, uSex);
                    jedis.disconnect();
                    jedis.close();
                    return JSON.toJSONString(toBeSend);
                } else {
                    jedis.disconnect();
                    jedis.close();
                    closeQuery(ret, stmt);
                    throw new TokenWrongException();
                }
            } else if (uPassword.equals(passwordRecv)) {
                logger.debug("Connected to redis");
                jedis.del(uID);
                jedis.set(uID, uuid);
                logger.debug("Binded {} with {}", uID, uuid);
                jedis.disconnect();
                jedis.close();
                var toBeSend = new LoginSend(uuid, uName, uType, uSex);
                return JSON.toJSONString(toBeSend);
            } else {
                jedis.close();
                closeQuery(ret, stmt, conn);
                throw new WrongPasswordException();
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        } finally {
            if (jedis != null) jedis.disconnect();
            try {
                closeQuery(ret, stmt, conn);
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }
        throw new WrongPasswordException();
    }
}

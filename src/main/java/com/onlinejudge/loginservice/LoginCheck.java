package com.onlinejudge.loginservice;

import com.alibaba.fastjson.JSON;
import com.onlinejudge.manservice.UserWithPasswd;
import com.onlinejudge.util.ClassEvent;
import com.onlinejudge.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static com.onlinejudge.util.DatabaseUtil.closeQuery;

public class LoginCheck extends ClassEvent {
    private String passwordRecv;
    private String uID;
    private static Logger logger = LoggerFactory.getLogger(LoginCheck.class);

    public LoginCheck(String passwordRecv, String uID) {
        this.passwordRecv = passwordRecv;
        this.uID = uID;
    }

    private Connection conn = null;
    private PreparedStatement stmt = null;
    private ResultSet ret = null;
    private Jedis jedis = null;
    @Override
    public String go() throws WrongPasswordException {
        String uuid = "-1";
        String uPassword = null;
        int uSex = -1;
        String uName = null;
        int uType = -1;
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
            if (cnt == 0){
                logger.info("Register for {} with password {}", uID, passwordRecv);
                UserWithPasswd CurrUser = new UserWithPasswd(
                        uID, "", uSex, 3, passwordRecv);
                CurrUser.updateUser();
                uPassword = passwordRecv;
                uName = "";
            }
            logger.debug("uID = {}", uID);
            logger.debug("uPassword = {}", uPassword);
            logger.debug("uType = {}" ,uType);
            uuid = UUID.randomUUID().toString().replace("-", "");
            logger.info("uuid = {}",uuid);
            // generate UUID
            // send information
            assert uPassword != null;
            if (uPassword.equals(passwordRecv)) {
                jedis = new Jedis("localhost");
                logger.debug("Connected to redis");
                jedis.del(uID);
                jedis.set(uID, uuid);
                logger.debug("Binded {} with {}", uID, uuid);
                jedis.disconnect();
                var toBeSend = new LoginSend(uuid, uName, uType, uSex);
                return JSON.toJSONString(toBeSend);
            } else {
                closeQuery(ret, stmt, conn);
                throw new WrongPasswordException();
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }finally{
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

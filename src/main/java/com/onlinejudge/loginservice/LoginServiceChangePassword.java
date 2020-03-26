package com.onlinejudge.loginservice;

import com.onlinejudge.userservice.UserServiceProperties;
import com.onlinejudge.userservice.UserServiceUpdateProperties;
import com.onlinejudge.util.BooleanEvent;
import com.onlinejudge.util.InternalException;
import org.jetbrains.annotations.TestOnly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

public class LoginServiceChangePassword extends BooleanEvent {
    private static Logger logger = LoggerFactory.getLogger(LoginServiceChangePassword.class);
    private String userID = null;
    private String userKey = null;
    private String newPassword = null;
    public LoginServiceChangePassword(String userID, String userKey, String newPassword){
        this.userID = userID;
        this.userKey = userKey;
        this.newPassword = newPassword;
    }
    private Jedis jedis = null;

    @TestOnly
    public static void main(String args[]) throws InternalException{
        var tmp = new LoginServiceChangePassword("sandstone12a@163.com", "524630", "2333");
        tmp.go();
    }

    public boolean go() throws InternalException {
        jedis = new Jedis("localhost");
        logger.debug("Connected to redis");
        String key = jedis.get("r" + userID);
        logger.debug("rightKey = {}, and userKey = {}", key, userKey);
        logger.info("query is ok, quit.");
        boolean result = userKey.equals(key);
        if (!result) return false;
        return (new UserServiceUpdateProperties(userID, newPassword, UserServiceProperties.changeUserPassword)).go();
    }
}

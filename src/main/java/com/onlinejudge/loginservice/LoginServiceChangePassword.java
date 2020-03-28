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
    private static final Logger logger = LoggerFactory.getLogger(LoginServiceChangePassword.class);
    private String userID;
    private String userKey;
    private String newPassword;
    public LoginServiceChangePassword(String userID, String userKey, String newPassword){
        this.userID = userID;
        this.userKey = userKey;
        this.newPassword = newPassword;
    }

    @TestOnly
    public static void main(String[] args) throws InternalException{
        var tmp = new LoginServiceChangePassword("limingjia1999@gmail.com", "111111", "2333");
        tmp.go();
    }

    public boolean go() throws InternalException {
        Jedis jedis = new Jedis("localhost");
        logger.debug("Connected to redis");
        String fetchID = "@" + userID;
        String key = jedis.get(fetchID);
        logger.debug("rightKey = {}, and userKey = {}", key, userKey);
        logger.info("query is ok, quit.");
        boolean result = userKey.equals(key);
        if (!result) return false;
        return (new UserServiceUpdateProperties(userID, newPassword, UserServiceProperties.changeUserPassword)).go();
    }
}

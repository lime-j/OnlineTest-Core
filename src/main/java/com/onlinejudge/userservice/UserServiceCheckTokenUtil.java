package com.onlinejudge.userservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

public class UserServiceCheckTokenUtil {
    // 这个类是用来检查token跟用户的token是否一致的,
    // 如果不一致, go() 函数返回false,
    // 否则返回true
    private static final Logger logger = LoggerFactory.getLogger(UserServiceCheckTokenUtil.class);
    public static void checkToken(String userID, String userToken) throws TokenWrongException {
        Jedis jedis = new Jedis("localhost");
        logger.info("connected to redis.");
        String token = jedis.get(userID);
        logger.debug("rightToken = {}, and userToken = {}", token, userToken);
        logger.info("query is ok, quit.");
        boolean flag = token.equals(userToken);
        jedis.disconnect();
        if (!flag) throw new TokenWrongException();
    }
}

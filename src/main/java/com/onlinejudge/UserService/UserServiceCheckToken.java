package com.onlinejudge.UserService;

import com.onlinejudge.util.BooleanEvent;
import redis.clients.jedis.Jedis;

import static com.onlinejudge.DaemonService.DaemonServiceMain.debugPrint;

public class UserServiceCheckToken extends BooleanEvent {
    // 这个类是用来检查token跟用户的token是否一致的,
    // 如果不一致, go() 函数返回false,
    // 否则返回true;
    private String userID, userToken;

    public UserServiceCheckToken(String userID, String userToken) {
        this.userID = userID;
        this.userToken = userToken;
    }

    public boolean go() {
        var jedis = new Jedis("localhost");
        System.out.println("[DBG]: UserServiceCheckToken, connected to redis.");
        String token = jedis.get(this.userID);
        System.out.println("[DBG]: UserServiceCheckToken, rightToken = " + token + ", and userToken = " + this.userToken);
        jedis.disconnect();
        debugPrint("UserServiceCheckToken, query is ok, quit.");
        return (token.equals(this.userToken));
    }
}

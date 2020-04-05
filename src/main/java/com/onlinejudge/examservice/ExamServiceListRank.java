package com.onlinejudge.examservice;

import com.onlinejudge.util.InternalException;
import com.onlinejudge.util.ListEvent;
import lombok.extern.log4j.Log4j2;
import redis.clients.jedis.Jedis;

import java.util.List;

import static com.alibaba.fastjson.JSON.parseArray;
import static com.alibaba.fastjson.JSON.toJSONString;

@Log4j2
public class ExamServiceListRank implements ListEvent<RankedUser> {
    private static final int REFRESH_INTERVAL = 120000;
    private boolean flag = true;
    private List<RankedUser> res = null;
    private String examID;
    private Jedis jedis = null;

    public ExamServiceListRank(String examID) {
        this.examID = examID;
        this.jedis = new Jedis("localhost");
    }

    @Override
    public List<RankedUser> go() throws InternalException {
        if (flag) res = ExamServiceGetRankedUser.getItem(examID);
        else {
            String dat = jedis.get(examID + "$$");
            res = parseArray(dat, RankedUser.class);
        }
        return res;
    }


    @Override
    public void beforeGo() throws InternalException {
        //
        var pair = ExamServiceGetContestTime.getItem(examID);
        long currentTime = System.currentTimeMillis();
        if (pair.getLeft().getTime() < currentTime && currentTime < pair.getRight().getTime() + REFRESH_INTERVAL) {
            flag = false;
            return;
        }
        log.info("connected to redis.");
        String time = jedis.get(examID);
        flag = (time == null || (Long.parseLong(time) - System.currentTimeMillis() >= REFRESH_INTERVAL));
    }

    @Override
    public void afterGo() throws InternalException {
        assert res != null;
        if (flag) {
            ExamServiceSetRank.setItem(res, examID);
            jedis.set(examID, Long.toString(System.currentTimeMillis()));
            jedis.set(examID + "$$", toJSONString(res));
        }
        jedis.disconnect();

    }
}

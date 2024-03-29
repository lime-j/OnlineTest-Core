package com.onlinejudge.predictservice;

import com.onlinejudge.util.InternalException;
import com.onlinejudge.util.ListEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.onlinejudge.predictservice.PredictServiceEbbinghauseUtil.getRecommandList;
import static com.onlinejudge.predictservice.PredictServiceTreeUtil.getItem;
import static com.onlinejudge.util.DatabaseUtil.closeQuery;
import static com.onlinejudge.util.DatabaseUtil.prepareStatement;
import static java.lang.Math.abs;
import static java.lang.Math.min;


public class PredictServiceListPredictedItem implements ListEvent<String> {
    private static final Logger log = LoggerFactory.getLogger(PredictServiceListPredictedItem.class);
    private static final int ITEM_VAL = 10;
    private final String userID;
    private int pviot;
    private int throttle;
    private UserStar me = null;
    private static final Comparator<PredictedItem> c = new Cmp();

    static class Cmp implements Comparator<PredictedItem> {
        @Override
        public int compare(PredictedItem t0, PredictedItem t1) {
            if (1e3 >= abs(t0.getInteresting() - t1.getInteresting())) {
                return -Double.compare(abs(t0.getChallenging()), abs(t1.getChallenging()));
            } else {
                return -Double.compare(t0.getInteresting(), t1.getInteresting());
            }
        }
    }

    public PredictServiceListPredictedItem(String userID, int pviot, int throttle) {
        this.userID = userID;
        this.pviot = pviot;
        this.throttle = throttle;
    }

    private PredictedItem getScore(String pir) {
        List<UserStar> lst = PredictServiceGetUserStar.getItem(pir);
        assert me != null;
        lst = lst.subList(0, min(10, lst.size() - 1));
        double result1 = 0;
        double result2 = 0;
        for (var it : lst) {
            result1 += UserStar.getSimilarity(me, it) * (it.getIsInteresting() / 5.0 - 0.5);
            result2 += UserStar.getSimilarity(me, it) * (it.getIsChallenging() / 5.0 - 0.5);
        }
        log.debug("result1 = {} result2 = {}",result1, result2);
        return new PredictedItem(pir, result1, result2);
    }

    @Override
    public List<String> go() {
        List<PredictedItem> result = new ArrayList<>();
        List<Pair<String, Double>> ebb = getRecommandList(pviot, userID);
        List<Pair<String, Double>> tree = getItem(ITEM_VAL - pviot, userID);
        log.debug("ebb = {}", ebb);
        log.debug("tree = {}", tree);
        for (var it : ebb) {
            result.add(getScore(it.getLeft()));
        }
        for (var it : tree) {
            result.add(getScore(it.getLeft()));
        }
        result.sort(c);
        List<String> finalRet = new ArrayList<>();
        int cnt = 0;
        for(var it : result){
            if (++ cnt >= throttle) break;
            finalRet.add(it.getExamID());
        }
        log.debug(String.valueOf(finalRet));
        return finalRet;
    }

    @Override
    public void beforeGo() {
        PreparedStatement stmt = null;
        ResultSet ret = null;
        int userRating = 0;
        boolean[] sim = PredictServiceAddUserStar.handle(userID);
        try {
            stmt = prepareStatement("select utype from userinfo where uid = ?");
            stmt.setString(1, userID);
            log.debug(String.valueOf(stmt));
            ret = stmt.executeQuery();
            int cnt = 0;
            while (ret.next()) {
                ++cnt;
                userRating = ret.getInt("utype");
            }
            assert cnt == 1;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                closeQuery(ret, stmt);
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }
        me = new UserStar(userID, "114514", userRating, 0, 0, sim);

    }

    @Override
    public void afterGo() throws InternalException {
        // do nothing
    }

}

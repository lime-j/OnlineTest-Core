package com.onlinejudge.predictservice;

import com.onlinejudge.util.InternalException;
import com.onlinejudge.util.ListEvent;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.google.common.primitives.Ints.min;
import static com.onlinejudge.predictservice.PredictServiceEbbinghauseProvider.getRecommandList;
import static com.onlinejudge.predictservice.PredictServiceTreeProvider.getItem;
import static com.onlinejudge.util.DatabaseUtil.closeQuery;
import static com.onlinejudge.util.DatabaseUtil.prepareStatement;

@Log4j2
public class PredictServiceListPredictedItem implements ListEvent<Pair<String, Double>> {
    private static final int ITEM_VAL = 10;
    private final String userID;
    private int pviot;
    private int throttle;
    private UserStar me = null;
    private static final Comparator<Pair<String, Double>> c = new PredictServiceEbbinghauseProvider.Cmp();

    public PredictServiceListPredictedItem(String userID, int pviot, int throttle) {
        this.userID = userID;
        this.pviot = pviot;
        this.throttle = throttle;
    }

    private double getScore(Pair<String, Double> pir) {
        List<UserStar> lst = PredictServiceGetUserStar.getItem(pir.getLeft());
        assert me != null;
        lst = lst.subList(0, min(5, lst.size() - 1));
        double result = 0;
        for (var it : lst){
            result += UserStar.getSimilarity(me,it);
        }
        return result;
    }

    @Override
    public List<Pair<String, Double>> go() throws InternalException {
        List<Pair<String, Double>> result = new ArrayList<>();
        List<Pair<String, Double>> ebb = getRecommandList(pviot, userID);
        List<Pair<String, Double>> tree = getItem(ITEM_VAL - pviot, userID);
        for (var it : ebb) {
            result.add(new ImmutablePair<>(it.getLeft(), getScore(it)));
        }
        for (var it : tree) {
            result.add(new ImmutablePair<>(it.getLeft(), getScore(it)));
        }
        result.sort(c);
        return result.subList(0, throttle);
    }

    @Override
    public void beforeGo() throws InternalException {
        PreparedStatement stmt = null;
        ResultSet ret = null;
        int userRating = 0;
        boolean[] sim = PredictServiceAddUserStar.handle(userID);
        try {
            stmt = prepareStatement("select utype from userinfo where uid = ?");
            stmt.setString(1, userID);
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

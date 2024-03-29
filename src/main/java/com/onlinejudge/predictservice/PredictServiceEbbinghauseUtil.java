package com.onlinejudge.predictservice;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.onlinejudge.util.DatabaseUtil.closeQuery;
import static com.onlinejudge.util.DatabaseUtil.prepareStatement;
import static java.lang.Math.pow;


public class PredictServiceEbbinghauseUtil {
    private static final Logger log = LoggerFactory.getLogger(PredictServiceEbbinghauseUtil.class);
    private static final Comparator<Pair<String, Double>> c = new Cmp();

    @Contract(pure = true)
    private PredictServiceEbbinghauseUtil() {
    }

    @Contract(pure = true)
    protected static double getAibinScore(long ox) {
        double x = (double) ox;
        return 1 / (1 - pow(0.56 * x, 0.06));
    }

    static class Cmp implements Comparator<Pair<String, Double>> {
        @Override
        public int compare(Pair<String, Double> stringDoublePair, Pair<String, Double> t1) {
            return -Double.compare(stringDoublePair.getValue(), t1.getValue());
        }
    }

    @NotNull
    protected static List<Pair<String, Double>> getRecommandList(int throttle, String userID) {
        PreparedStatement stmt = null;
        ResultSet res = null;
        List<Pair<String, Double>> lst = new ArrayList<>();
        Map<String, Long> visMap = new HashMap<>();
        Set<String> examSet = new HashSet<>();
        try {
            stmt = prepareStatement("select * from exam e, examperm ep where e.iscontest = 0 and sid = ? and e.eid = ep.eid");
            stmt.setString(1, userID);
            res = stmt.executeQuery();
            while (res.next()) {
                String examID = res.getString("eid");
                long curr = res.getTimestamp("time").getTime();
                long currMax = visMap.getOrDefault(examID, 0L);
                if (curr > currMax) {
                    visMap.put(examID, curr);
                    examSet.add(examID);
                }
            }
            for (var examID : examSet) {
                lst.add(new ImmutablePair<>(examID, getAibinScore(visMap.get(examID))));
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                closeQuery(res, stmt);
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }
        lst.sort(c);
        List<Pair<String, Double>> finalList = new ArrayList<>();
        for (int i = 0; i < Math.max(throttle, lst.size()); ++i) {
            finalList.add(lst.get(i));
        }
        finalList.sort(c);
        return finalList;
    }
}

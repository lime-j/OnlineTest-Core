package com.onlinejudge.predictservice;

import com.onlinejudge.util.DatabaseUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;


public class PredictServiceTreeUtil {

    private static final Logger log = LoggerFactory.getLogger(PredictServiceTreeUtil.class);
    // Happy tree friend!
    // (but this is actually a DAG, not tree!
    private static final Map<String, List<Edge>> EDGE = new HashMap<>();
    private static final String ROOT = "/tree.xml";
    private static final Comparator<Pair<String, Double>> c = new PredictServiceEbbinghauseUtil.Cmp();
    private static final int K = 1;
    @NotNull
    private static Set<String> getUserStudyStatus(String userID) {
        PreparedStatement stmt = null;
        ResultSet ret = null;
        Set<String> result = new TreeSet<>();
        try {
            stmt = DatabaseUtil.prepareStatement("select ep.eid from exam e, examperm ep where e.iscontest = 0 and ep.sid = ?");
            stmt.setString(1, userID);
            ret = stmt.executeQuery();
            while (ret.next()) {
                result.add(ret.getString(1));
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                DatabaseUtil.closeQuery(ret, stmt);
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }
        log.debug(String.valueOf(result));
        return result;
    }

    static  {
        InputStream temp = PredictServiceTreeUtil.class.getResourceAsStream(ROOT);
        Scanner cin = new Scanner(temp);
        int cnt = 0;
        while (cin.hasNext()) {
            String from = cin.next();
            String to = cin.next();
            log.debug("from = {}, to = {}", from, to);
            double val = cin.nextDouble();
            if (EDGE.getOrDefault(from, null) == null) {
                List<Edge> edg = new ArrayList<>();
                edg.add(new Edge(to, val));
                EDGE.put(from, edg);
            } else EDGE.get(from).add(new Edge(to, val));
            ++ cnt;
        }
        log.debug(String.valueOf(cnt));

    }

    protected static List<Pair<String, Double>> getItem(int throttle, String userID) {
        List<Pair<String, Double>> result = new ArrayList<>();
        Set eids = getUserStudyStatus(userID);
        for (var eid : eids) {
            boolean flag = true;
            var map = EDGE.getOrDefault(eid, null);
            assert null != map;
            for (var toEid : map) {
                if (eids.contains(toEid.getTo())) {
                    flag = false;
                    break;
                }
            }
            if (!flag) {
                for (var toEid : map) {
                    result.add(new ImmutablePair<>(toEid.getTo(), K / toEid.getWeight()));
                }
            }
        }
        List<Pair<String, Double>> sub = new ArrayList<>();
        result.sort(c);
        int cnt = 0;
        for (var it : result) {
            ++cnt;
            if (cnt <= throttle) sub.add(it);
            else break;
        }
        return sub;
    }

}
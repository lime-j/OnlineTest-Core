package com.onlinejudge.predictservice;

import com.onlinejudge.util.DatabaseUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static jdk.xml.internal.SecuritySupport.getResourceAsStream;

@Log4j2
public class PredictServiceTreeProvider {
    // Happy tree friend!
    // (but this is actually a DAG, not tree!
    private static final Map<String, List<Edge>> EDGE = new HashMap<>();
    private static final String ROOT = "ROOT";

    @NotNull
    @Contract(pure = true)
    private static List<Pair<String, Double>> getItem() {
        List<Pair<String, Double>> result = new ArrayList<>();
        result.add(new ImmutablePair<>("114514", 1919.810));
        return result;
    }

    private static Set<String> getUserStudyStatus(String userID) {
        PreparedStatement stmt = null;
        ResultSet ret = null;
        Set<String> result = new TreeSet<>();
        try {
            stmt = DatabaseUtil.prepareStatement("select ep.eid from exam e, examperm ep where e.iscontest = 0 and ep.sid = 'limingjia1999@gmail.com'");
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
        return result;
    }

    public PredictServiceTreeProvider() {
        InputStream temp = getResourceAsStream("/resources/config.properties");
        Scanner cin = new Scanner(temp);
        while (cin.hasNext()) {
            String from = cin.next();
            String to = cin.next();
            double val = cin.nextDouble();
            if (EDGE.getOrDefault(from, null) == null) {
                List<Edge> edg = new ArrayList<>();
                edg.add(new Edge(to, val));
                EDGE.put(from, edg);
            } else EDGE.get(from).add(new Edge(to, val));
        }
    }
    private static List<PredictedItem> getItem(String userID) {
        List<PredictedItem> result = new ArrayList<>();
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
                    result.add(new PredictedItem(toEid.getTo(), toEid.getWeight()));
                }
            }
        }
        return result;
    }

}

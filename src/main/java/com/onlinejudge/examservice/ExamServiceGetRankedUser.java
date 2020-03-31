package com.onlinejudge.examservice;

import com.onlinejudge.util.InternalException;
import com.onlinejudge.util.ListerUtil;
import com.onlinejudge.util.Provider;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import static com.onlinejudge.util.DatabaseUtil.closeQuery;
import static com.onlinejudge.util.DatabaseUtil.prepareStatement;
import static java.lang.Integer.getInteger;
import static java.util.Collections.sort;

@Log4j2
public class ExamServiceGetRankedUser implements Provider {

    @NotNull
    private static List<String> getParticipantsID(String examID) {
        return new ListerUtil<String>("select sid from examperm where eid = ?", examID, "UserserviceGetPants").getResultList();
    }

    private static boolean isValidSubmission(@NotNull Pair<Timestamp, Timestamp> contestTimes, @NotNull Timestamp submissionTime) {
        Timestamp start = contestTimes.getKey();
        Timestamp end = contestTimes.getValue();
        return submissionTime.after(start) && submissionTime.before(end);
    }

    @NotNull
    protected static List<RankedUser> getRank(@NotNull List<String> userIDs, String examID, @NotNull Pair<Timestamp, Timestamp> times) {
        PreparedStatement stmt = null;
        ResultSet res = null;
        Timestamp start = times.getKey();
        List<RankedUser> ret = new ArrayList<>();
        try {
            stmt = prepareStatement("select score,pid, stime from submission where eid = ? and sid = ?");
            stmt.setString(1, examID);
            for (var userID : userIDs) {
                Map<String, Integer> scoreMap = new HashMap<>();
                Map<String, Timestamp> timeMap = new HashMap<>();
                Set<String> pidSet = new HashSet<>();
                stmt.setString(2, userID);
                log.debug(stmt);
                res = stmt.executeQuery();
                while (res.next()) {
                    int score = res.getInt("score");
                    String pid = res.getString("pid");
                    Timestamp stime = res.getTimestamp("stime");
                    if (score == 0) continue;
                    if (isValidSubmission(times, stime)) {
                        pidSet.add(pid);
                        if (scoreMap.getOrDefault(pid, -1) != -1) {
                            if (scoreMap.get(pid) < score) {
                                scoreMap.put(pid, score);
                                timeMap.put(pid, stime);
                            }
                        } else {
                            scoreMap.put(pid, score);
                            timeMap.put(pid, stime);
                        }
                    }
                }
                res.close();
                ret.add(new RankedUser(userID, getPenalty(pidSet, scoreMap, timeMap, start), scoreMap.size(), 0));
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
        sort(ret);
        int curRank = 0;
        for (var pan : ret) {
            ++curRank;
            pan.setRank(curRank);
        }
        return ret;
    }



    private static int getPenalty(@NotNull Set<String> pidSet, Map<String, Integer> scoreMap, Map<String, Timestamp> timeMap, Timestamp start) {
        int penalty = 0;
        for (var pid : pidSet) {
            long submitTime = timeMap.get(pid).getTime();
            long startTime = start.getTime();
            long result = (submitTime - startTime) / 60000L;
            penalty += getInteger(Long.toString(result)) * scoreMap.get(pid);
        }
        return penalty;
    }

    @NotNull
    public static List<RankedUser> getItem(String examID) throws InternalException {
        var pair = ExamServiceGetContestTime.getItem(examID);
        var pansID = getParticipantsID(examID);
        return getRank(pansID, examID, pair);
    }
}

package com.onlinejudge.examservice;

import com.onlinejudge.examservice.ExamServiceGetRating.Participant;
import com.onlinejudge.util.InternalException;
import com.onlinejudge.util.ListerUtil;
import com.onlinejudge.util.Provider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.*;

import static com.onlinejudge.util.DatabaseUtil.*;
import static java.lang.Integer.getInteger;
import static java.util.Collections.sort;

@Log4j2
public class ExamServiceGetParticipants implements Provider {
    @NotNull
    private static Pair<Timestamp, Timestamp> isValidExamID(String examID) throws InternalException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            conn = getConnection();
            stmt = prepareStatement("select estart, eend, eid from exam where eid = ?");
            stmt.setString(1, examID);
            res = stmt.executeQuery();
            int cnt = 0;
            Timestamp start = null;
            Timestamp end = null;
            while (res.next()) {
                ++cnt;
                start = res.getTimestamp("estart");
                end = res.getTimestamp("eend");
            }
            if (start == null || end == null)
                throw new InternalException("start or end == null, database may has been broken");
            if (cnt != 1) throw new InternalException("There is no such exam.");
            closeQuery(res, stmt, conn);
            return new ImmutablePair<>(start, end);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            try {
                closeQuery(res, stmt, conn);
            } catch (SQLException ex) {
                log.error(ex.getMessage(), ex);
            }
            throw new InternalException("SQL Error");
        }
    }

    private static List<String> getParticipantsID(String examID) {
        return new ListerUtil<String>("select sid from examperm where eid = ?", examID, "UserserviceGetPants").getResultList();
    }

    private static boolean isValidSubmission(@NotNull Pair<Timestamp, Timestamp> contestTimes, @NotNull Timestamp submissionTime) {
        Timestamp start = contestTimes.getKey();
        Timestamp end = contestTimes.getValue();
        return submissionTime.after(start) && submissionTime.before(end);
    }

    @NotNull
    @SneakyThrows
    private static List<RankedUser> getRank(@NotNull List<String> userIDs, String examID, @NotNull Pair<Timestamp, Timestamp> times) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet res = null;
        Timestamp start = times.getKey();
        List<RankedUser> ret = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = prepareStatement("select score,pid, stime from submission where eid = ? and sid = ?");
            stmt.setString(1, examID);
            for (var userID : userIDs) {
                Map<String, Integer> scoreMap = new HashMap<>();
                Map<String, Timestamp> timeMap = new HashMap<>();
                Set<String> pidSet = new HashSet<>();
                stmt.setString(2, userID);
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
            closeQuery(res, stmt, conn);
        }
        sort(ret);
        int curRank = 0;
        for (var pan : ret) {
            ++curRank;
            pan.setRank(curRank);
        }
        return ret;
    }

    @SneakyThrows
    private static void setRank(List<RankedUser> pans, String examID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = prepareStatement("update examperm set rank = ? where sid = ? and eid = ?");
            stmt.setString(3, examID);
            for (var pan : pans) {
                stmt.setString(1, Integer.toString(pan.getRank()));
                stmt.setString(2, pan.getUserID());
                int status = stmt.executeUpdate();
                log.info("status = {}", status);
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            //e.pTrace();
        } finally {
            closeUpdate(stmt, conn);
        }
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

    private static List<Participant> getRankedPartcipans(String examID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet ret = null;
        List<Participant> pans = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = prepareStatement("select uname, sid, utype as old_rating,`rank` from examperm ex, userinfo ui where ex.sid = ui.uid and ex.eid = ?");
            stmt.setString(1, examID);
            ret = stmt.executeQuery();
            while (ret.next()) {
                String sid = ret.getString("sid");
                int oldRating = ret.getInt("old_rating");
                int rank = ret.getInt("rank");
                String sname = ret.getString("uname");
                pans.add(new Participant(rank, sname, sid, oldRating, 0, 0, 0));
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                closeQuery(ret, stmt, conn);
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }
        return pans;
    }

    @NotNull
    public static List<Participant> getItem(String examID) throws InternalException {
        var pair = isValidExamID(examID);
        var pansID = getParticipantsID(examID);
        var listedPans = getRank(pansID, examID, pair);
        setRank(listedPans, examID);
        return getRankedPartcipans(examID);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class RankedUser implements Comparable<RankedUser> {
        String userID;
        int penalty;
        int solved;
        int rank;

        @Override
        public int compareTo(@NotNull RankedUser o) {
            if (solved != o.solved) {
                return -Integer.compare(solved, o.solved);
            } else return Integer.compare(penalty, o.penalty);
        }
    }
}

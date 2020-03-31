package com.onlinejudge.examservice;

import com.onlinejudge.util.IntegerEvent;
import com.onlinejudge.util.InternalException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.onlinejudge.util.DatabaseUtil.closeQuery;
import static com.onlinejudge.util.DatabaseUtil.prepareStatement;

@Getter
@Setter
public class ExamServiceQueryStudentScore implements IntegerEvent {
    private String studentID;
    private String examID;
    private int queryType;
    private Connection conn = null;
    private PreparedStatement stmt = null;
    private ResultSet result = null;
    private static final Logger logger = LoggerFactory.getLogger(ExamServiceQueryStudentScore.class);

    public ExamServiceQueryStudentScore(String examID, String studentID, int queryType) {
        this.studentID = studentID;
        this.examID = examID;
        this.queryType = queryType;
    }

    private boolean judge(Timestamp time, Timestamp startTime, Timestamp endTime) {
        boolean res;
        if (this.queryType == 1) {
            res = time.after(endTime) || time.before(startTime);
        } else {
            res = time.before(endTime) && time.after(startTime);
        }
        return res;
    }

    @NotNull
    @Contract(" -> new")
    private Pair<Timestamp, Timestamp> checkExamID() throws SQLException, InternalException {
        Timestamp startTime = null;
        Timestamp endTime = null;
        stmt = prepareStatement("select * from exam e where e.eid = ?");
        stmt.setString(1, this.examID);
        result = stmt.executeQuery();
        int cnt = 0;

        while (result.next()) {
            ++cnt;
            startTime = result.getTimestamp("estart");
            endTime = result.getTimestamp("eend");
        }
        if (cnt == 0) {
            logger.warn("ExamID:{} doesn''t exist.", this.examID);
            throw new InternalException("exam doesn't exist");
        } else if (cnt > 1) {
            logger.warn("There are one more exam with ExamID:{}, something wrong must happened to database", this.examID);
            logger.warn("Please, check the database for further information");
            throw new InternalException("database corrupted");
        }
        result.close();
        stmt.close();
        return new ImmutablePair<>(startTime, endTime);
    }

    @NotNull
    private List<String> getProblemID() throws SQLException {
        List<String> probIDs = new ArrayList<>();
        // Query 2: querying the exam and examprob, search for the probID(s)
        stmt = prepareStatement("select ep.pid from exam e, examprob ep where e.eid = ep.eid and e.eid = ?");
        stmt.setString(1, this.examID);
        int cnt = 0;
        result = stmt.executeQuery();
        while (result.next()) {
            ++cnt;
            probIDs.add(result.getString("pid"));
        }
        if (cnt == 0) {
            logger.warn("Oops... Seems that there are no problem(s) in this exam, what's your problem?");
        }
        result.close();
        stmt.close();
        return probIDs;
    }

    private int getScore(@NotNull Pair<Timestamp, Timestamp> pair, @NotNull List<String> probIDs) throws SQLException {
        // Query 3: querying the submission, search for student's last submission.
        int ret = 0;
        HashMap<String, Integer> score = new HashMap<>();
        Timestamp startTime = pair.getLeft();
        Timestamp endTime = pair.getRight();
        int cnt = probIDs.size();
        stmt = prepareStatement("select * from submission s where s.suid = ? and s.seid = ? and s.spid = ? ");
        for (int i = 1; i <= cnt; ++i) {
            //qry = String.format("select * from submission s where s.suid = '%s' and s.seid = '%s' and s.spid = '%s'", this.studentID, this.examID, probIDs.get(i - 1))
            stmt.setString(1, this.studentID);
            stmt.setString(2, this.examID);
            stmt.setString(3, probIDs.get(i - 1));
            logger.info(stmt.toString());
            result = stmt.executeQuery();
            int co = 0;
            while (result.next()) {
                Timestamp time = result.getTimestamp("stime");
                if (judge(time, startTime, endTime)) {
                    continue;
                }
                ++co;
                var cscore = result.getInt("sscore");
                String pid = result.getString("spid");
                score.putIfAbsent(pid, cscore);
                var pscore = score.get(pid);
                if (pscore < cscore) score.replace(pid, cscore);
            }
            logger.info("find {} results", co);
        }
        for (String key : probIDs) score.putIfAbsent(key, 0);
        for (String key : probIDs) ret += score.get(key);
        closeQuery(result, stmt);
        return ret;
    }

    @Override
    public void beforeGo() {
        // do nothing
    }

    @Override
    public void afterGo() {
        // do nothing
    }

    public int go() throws InternalException {
        try {
            return getScore(checkExamID(), getProblemID());
        } catch (SQLException se) {
            logger.error("Something wrong in SQL", se);
            try {
                closeQuery(result, stmt, conn);
            } catch (SQLException e) {
                logger.error("Something wrong while closing", e);
            }
            return -1;
        }
    }
}

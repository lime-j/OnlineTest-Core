package com.onlinejudge.examservice;

import com.onlinejudge.util.DatabaseUtil;
import com.onlinejudge.util.IntegerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.onlinejudge.util.DatabaseUtil.closeQuery;
import static com.onlinejudge.util.DatabaseUtil.prepareStatement;

public class ExamServiceQueryStudentScore extends IntegerEvent {
    private String studentID, examID;
    private int queryType;
    private static final Logger logger = LoggerFactory.getLogger(ExamServiceQueryStudentScore.class);

    public ExamServiceQueryStudentScore(String examID, String studentID, int queryType) {
        this.studentID = studentID;
        this.examID = examID;
        this.queryType = queryType;
    }

    private boolean judge(Timestamp time, Timestamp startTime, Timestamp endTime) {
        if (this.queryType == 1) {
            return time.after(endTime) || time.before(startTime);
        } else return time.before(endTime) && time.after(startTime);
    }
    public int go() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet result = null;
        try {
            conn = DatabaseUtil.getConnection();
            stmt = prepareStatement("select * from exam e where e.eid = ?");
            stmt.setString(1, this.examID);

            // Query 1: get exam from examID;
            result = stmt.executeQuery();
            int cnt = 0;
            Timestamp startTime = null, endTime = null;
            while (result.next()) {
                ++cnt;
                startTime = result.getTimestamp("estart");
                endTime = result.getTimestamp("eend");
            }
            if (cnt == 0) {
                logger.warn(MessageFormat.format("ExamID:{0} doesn''t exist.", this.examID));
                return -1;
            } else if (cnt > 1) {
                logger.warn(MessageFormat.format("There are one more exam with ExamID:{0}, something wrong must happened to database", this.examID));
                logger.warn("Please, check the database for further information");
                return -1;
            }
            result.close();
            //Exam currentExam = new Exam();
            //String qry = String.format();

            // Query 2: querying the exam and examprob, search for the probID(s)
            stmt.close();
            stmt = prepareStatement("select ep.pid from exam e, examprob ep where e.eid = ep.eid and e.eid = ?");
            stmt.setString(1, this.examID);
            cnt = 0;
            result = stmt.executeQuery();
            List<String> probIDs = new ArrayList<>();
            while (result.next()) {
                ++cnt;
                probIDs.add(result.getString("pid"));
            }
            if (cnt == 0) {
                logger.warn("Oops... Seems that there are no problem(s) in this exam, what's your problem?");
                return -1;
            }
            result.close();
            stmt.close();

            // Query 3: querying the submission, search for student's last submission.
            HashMap<String, Integer> score = new HashMap<>();
            stmt = prepareStatement("select * from submission s where s.suid = ? and s.seid = ? and s.spid = ? ");
            for (int i = 1; i <= cnt; ++i) {
                //qry = String.format("select * from submission s where s.suid = '%s' and s.seid = '%s' and s.spid = '%s'", this.studentID, this.examID, probIDs.get(i - 1));
                stmt.setString(1, this.studentID);
                stmt.setString(2, this.examID);
                stmt.setString(3, probIDs.get(i - 1));
                logger.debug(stmt.toString());
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

            }
            int ret = 0;
            for (String key : probIDs) score.putIfAbsent(key, 0);
            for (String key : probIDs) ret += score.get(key);
            closeQuery(result, stmt, conn);
            return ret;
        } catch (SQLException se) {
            logger.error("Something wrong in SQL",se);
            try {
                closeQuery(result, stmt, conn);
            } catch (SQLException e) {
                logger.error("Something wrong while closing",e);
            }
            return -1;
        }
    }

    public String getExamID() {
        return this.examID;
    }

    public void setExamID(String examID) {
        this.examID = examID;
    }

    public String getStudentID() {
        return this.studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public int getQueryType() {
        return this.queryType;
    }

    public void setQueryType(int queryType) {
        this.queryType = queryType;
    }
}

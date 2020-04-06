package com.onlinejudge.problemservice;

import com.onlinejudge.util.ListEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.onlinejudge.util.DatabaseUtil.*;


public class ProblemServiceListSubmission implements ListEvent<Submission> {
    private final String examID;
    private final String probID;
    private final String userID;
    private static final Logger logger = LoggerFactory.getLogger(ProblemServiceListSubmission.class);

    public ProblemServiceListSubmission(String examID, String probID, String userID) {
        this.examID = examID;
        this.probID = probID;
        this.userID = userID;
    }

    @Override
    public void beforeGo() {
        // do nothing
    }

    @Override
    public void afterGo() {
        // do nothing
    }

    @Override
    public List<Submission> go() {
        String cmd;
        PreparedStatement stmt = null;
        Connection conn = null;
        ResultSet subTextSet = null;
        try {
            conn = getConnection();
            cmd = "SELECT * FROM submission WHERE spid= ?  AND seid= ? AND suid = ? ORDER BY stime";
            stmt = prepareStatement(cmd);
            stmt.setString(1, probID);
            stmt.setString(2, examID);
            stmt.setString(3, userID);
            logger.info(stmt.toString());
            subTextSet = stmt.executeQuery();
            List<Submission> result = new ArrayList<>();
            while (subTextSet.next()) {
                Submission curr = new Submission(
                        subTextSet.getString("sid"), subTextSet.getString("stext"), subTextSet.getString("stime"),
                        subTextSet.getString("suid"), this.probID, subTextSet.getInt("sscore"), this.examID
                );
                result.add(curr);
            }
            return result;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                closeQuery(subTextSet, stmt, conn);
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return new ArrayList<>();
    }
}

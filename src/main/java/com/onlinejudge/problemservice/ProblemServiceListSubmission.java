package com.onlinejudge.problemservice;

import com.onlinejudge.util.ListEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.onlinejudge.util.DatabaseUtil.*;

public class ProblemServiceListSubmission extends ListEvent<Submission> {
    private final String examID;
    private final String probID;
    private final String userID;

    public ProblemServiceListSubmission(String examID, String probID, String userID) {
        this.examID = examID;
        this.probID = probID;
        this.userID = userID;
    }

    @Override
    public List<Submission> go() {
        String cmd = "";
        PreparedStatement stmt = null;
        Connection conn = null;
        ResultSet subTextSet = null;
        try {
            //Connection conn = getConnection();
            conn = getConnection();
            cmd = "select userinfo.uname, submission.sid, submission.stext, submission.stime, submission.suid, submission.sscore " + "from userinfo, submission " + "where spid= ?  and seid= ? and userinfo.uid = ?";
            stmt = prepareStatement(cmd);
            stmt.setString(1, examID);
            stmt.setString(2, probID);
            stmt.setString(3, userID);
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
            System.out.println("SQL: " + cmd);
            e.printStackTrace();
        } finally {
            try {
                closeQuery(subTextSet, stmt, conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

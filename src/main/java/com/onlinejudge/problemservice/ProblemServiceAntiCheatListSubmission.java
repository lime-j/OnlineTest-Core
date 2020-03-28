package com.onlinejudge.problemservice;

import com.onlinejudge.util.ListEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.onlinejudge.util.DatabaseUtil.*;

public class ProblemServiceAntiCheatListSubmission extends ListEvent {
    private final String examID;
    private final String probID;

    public ProblemServiceAntiCheatListSubmission(String examID, String probID) {
        this.examID = examID;
        this.probID = probID;
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
            cmd = String.format("select userinfo.uname, submission.sid, submission.stext, submission.stime, submission.suid, submission.sscore " +
                    "from userinfo, submission " +
                    "where spid='%s' and seid='%s' and sscore > 0 and userinfo.uid = submission.suid", this.probID, this.examID);
            stmt = prepareStatement(cmd);
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

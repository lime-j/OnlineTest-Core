package com.onlinejudge.problemservice;

import com.onlinejudge.util.ListEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.onlinejudge.util.DatabaseUtil.*;

import static com.onlinejudge.daemonservice.DaemonServiceMain.*;

public class ProblemServiceSubjectiveSubReturn extends ListEvent {
    private String examID, probID;
    private int probScore;

    public ProblemServiceSubjectiveSubReturn(String examID, String probID) {
        this.examID = examID;
        this.probID = probID;
    }

    @Override
    public List<SubjectiveSubmission> go() {
        try {
            // 返回客观题的提交
            // requestType: subjectSubList
            Connection conn = getConnection();
            PreparedStatement stmt = null;
            stmt = prepareStatement("select * from problem where pid=?");
            stmt.setString(1, this.probID);
            debugPrint(stmt.toString());
            var probData = stmt.executeQuery();
            while (probData.next()) {
                this.probScore = probData.getInt("pscore");
            }
            stmt = prepareStatement("select userinfo.uname, submission.sid, submission.stext, submission.stime, submission.suid, submission.sscore " +
                    " from userinfo, submission " +
                    " where " +
                    " spid=? and seid=? and userinfo.uid=submission.suid and submission.sjudged=0");
            stmt.setString(1, this.probID);
            stmt.setString(2, this.examID);
            debugPrint("[ProblemService]: Subject Submission Find: SQL: " + stmt.toString());
            var resultset = stmt.executeQuery();
            List<SubjectiveSubmission> resltlist = new ArrayList<>();
            while (resultset.next()) {
                SubjectiveSubmission curr = new SubjectiveSubmission(
                        resultset.getString("sid"), resultset.getString("stext"),
                        resultset.getString("suid"), this.probID, this.examID, this.probScore
                );
                resltlist.add(curr);
                System.out.println(String.format("ProblemService: SubjectFind - \n\tpid=%s sid=%s", this.probID, curr.getSubID()));
            }
            closeQuery(resultset, stmt, conn);
            return resltlist;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

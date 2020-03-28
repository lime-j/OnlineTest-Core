package com.onlinejudge.problemservice;

import com.onlinejudge.util.ListEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.onlinejudge.util.DatabaseUtil.*;

public class ProblemServiceSubjectiveSubReturn extends ListEvent {
    private final String examID;
    private final String probID;
    private int probScore;
    private static final Logger logger = LoggerFactory.getLogger(ProblemServiceSubjectiveSubReturn.class);

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
            PreparedStatement stmt;
            stmt = prepareStatement("select * from problem where pid=?");
            stmt.setString(1, this.probID);
            logger.info(stmt.toString());
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
            logger.debug("[problemservice]: Subject Submission Find: SQL: " + stmt.toString());
            var resultset = stmt.executeQuery();
            List<SubjectiveSubmission> resltlist = new ArrayList<>();
            while (resultset.next()) {
                SubjectiveSubmission curr = new SubjectiveSubmission(
                        resultset.getString("sid"), resultset.getString("stext"),
                        resultset.getString("suid"), this.probID, this.examID, this.probScore
                );
                resltlist.add(curr);
                logger.debug(String.format("problemservice: SubjectFind - \n\tpid=%s sid=%s", this.probID, curr.getSubID()));
            }
            closeQuery(resultset, stmt, conn);
            return resltlist;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

package com.onlinejudge.ExamService;

import com.onlinejudge.util.BooleanEvent;
import com.onlinejudge.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.onlinejudge.util.DatabaseUtil.closeUpdate;

public class ExamServiceDeleteProblemFromExam extends BooleanEvent {
    private String examID, probID;

    public ExamServiceDeleteProblemFromExam(String examID, String probID) {
        this.examID = examID;
        this.probID = probID;
    }

    public boolean go() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseUtil.getConnection();
            String qry = String.format("delete from examprob where pid = '%s' and eid = '%s';", this.examID, this.probID);
            stmt = DatabaseUtil.prepareStatement(qry);
            //stmt.execute("use onlinejudge");
            stmt.execute();
            closeUpdate(stmt, conn);
            return true;
        } catch (SQLException e) {
            try {
                closeUpdate(stmt, conn);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }

    }

    public String getProbID() {
        return this.probID;
    }

    public void setProbID(String probID) {
        this.probID = probID;
    }

    public String getExamID() {
        return this.examID;
    }

    public void setExamID(String examID) {
        this.examID = examID;
    }
}

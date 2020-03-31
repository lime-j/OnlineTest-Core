package com.onlinejudge.examservice;

import com.onlinejudge.util.BooleanEvent;
import com.onlinejudge.util.DatabaseUtil;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.onlinejudge.util.DatabaseUtil.closeUpdate;

@Getter
@Setter
public class ExamServiceDeleteProblemFromExam implements BooleanEvent {
    private String examID;
    private String probID;

    public ExamServiceDeleteProblemFromExam(String examID, String probID) {
        this.examID = examID;
        this.probID = probID;
    }

    @Override
    public void beforeGo() {
        // do nothing
    }

    @Override
    public void afterGo() {
        // do nothing
    }

    public boolean go() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseUtil.getConnection();
            String qry = String.format("delete from examprob where pid = '%s' and eid = '%s';", this.examID, this.probID);
            stmt = DatabaseUtil.prepareStatement(qry);
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
}

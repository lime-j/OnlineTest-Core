package com.onlinejudge.ExamService;

import com.onlinejudge.util.BooleanEvent;
import com.onlinejudge.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.onlinejudge.DaemonService.DaemonServiceMain.debugPrint;
import static com.onlinejudge.util.DatabaseUtil.closeUpdate;
import static com.onlinejudge.util.DatabaseUtil.prepareStatement;

public class ExamServiceDeleteExam extends BooleanEvent {
    public String examID;

    public ExamServiceDeleteExam(String examID) {
        this.examID = examID;
    }

    public boolean go() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseUtil.getConnection();
            //stmt.execute("use onlinejudge");
            String qry = String.format("delete from exam where eid = '%s';", this.examID);
            stmt = prepareStatement(qry);
            debugPrint("ExamServiceDeleteExam, " + qry);
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

package com.onlinejudge.ExamService;

import com.onlinejudge.util.ListEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.onlinejudge.DaemonService.DaemonServiceMain.debugPrint;
import static com.onlinejudge.util.DatabaseUtil.*;

public class ExamServiceListExamStudent extends ListEvent {
    public String examID;

    public ExamServiceListExamStudent(String examID) {
        this.examID = examID;
    }

    public List<ExamServiceListedStudent> go() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet queryResult = null;
        try {
            //   Class.forName(JDBC_DRIVER);
            conn = getConnection();
            String qry = String.format("select sid, uname from exam e, examperm ep, userinfo u where u.uid = ep.sid and e.eid = ep.eid and u.utype = 3 and e.eid = '%s'", examID);
            stmt = prepareStatement(qry);
            debugPrint("ExamServiceListExamStudent, conn and stmt settled.");
            //stmt.executeQuery("use onlinejudge");
            debugPrint("ExamServiceListExamStudent, qry = " + qry);
            queryResult = stmt.executeQuery();
            int cnt = 0;
            List<ExamServiceListedStudent> resultList = new ArrayList<>();
            while (queryResult.next()) {
                ++cnt;
                String sid = queryResult.getString("sid");
                var uname = queryResult.getString("uname");
                var result = new ExamServiceListedStudent(sid, uname);
                resultList.add(result);
            }
            closeQuery(queryResult, stmt, conn);
            System.out.println("[DBG]:ExamServiceListExamStudent,  find" + cnt + "result(s)");
            return resultList;
        } catch (SQLException sqlException) {
            try {
                closeQuery(queryResult, stmt, conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            sqlException.printStackTrace();
            return new ArrayList<>();
        }
    }
}
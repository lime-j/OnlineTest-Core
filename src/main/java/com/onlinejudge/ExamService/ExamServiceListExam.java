package com.onlinejudge.ExamService;

import com.onlinejudge.util.DatabaseUtil;
import com.onlinejudge.util.ListEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.onlinejudge.DaemonService.DaemonServiceMain.debugPrint;
import static com.onlinejudge.util.DatabaseUtil.*;

public class ExamServiceListExam extends ListEvent {
    private String userID;

    public ExamServiceListExam(String userID) {
        this.userID = userID;
    }

    public List<Exam> go() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseUtil.getConnection();
            debugPrint("ExamServiceListExam, conn and stmt created.");
            //stmt.execute("use onlinejudge");
            String qry = String.format("select e.ename, e.eid, e.ename, e.estart, e.eend, e.etext, e.esubject, ep.sid from examperm ep, exam e where e.eid = ep.eid and ep.sid = '%s' \n", this.userID);
            stmt = prepareStatement(qry);
            debugPrint("ExamServiceListExam, qry =" + qry);
            var queryResult = stmt.executeQuery();
            int cnt = 0;
            List<Exam> resultList = new ArrayList<>();
            while (queryResult.next()) {
                ++cnt;
                String eTitle = queryResult.getString("ename");
                //java.util.Date tspToDate = new java.util.Date(new java.sql.Timestamp(System.currentTimeMillis()).getTime());
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                var eStartTime = formatter.format(new java.util.Date(queryResult.getTimestamp("estart").getTime()));
                var eEndTime = formatter.format(new java.util.Date(queryResult.getTimestamp("eend").getTime()));
                var eID = queryResult.getString("eid");
                var userID = queryResult.getString("sid");
                var eText = queryResult.getString("etext");
                var eSubject = queryResult.getString("esubject");
                resultList.add(new Exam(eID, eTitle, userID, eStartTime, eEndTime, eText, eSubject));
                debugPrint("ExamServiceListExam, ename = " + eTitle
                        + ", estart = " + eStartTime
                        + ",eend = " + eEndTime
                        + ",eID = " + eID
                        + ",userID = " + userID
                        + "etext = " + eText
                        + "esubject = " + eSubject
                );
            }
            closeQuery(queryResult, stmt, conn);
            System.out.println("[DBG]:ExamServiceListExam, find" + cnt + "result(s)");
            return resultList;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            try {
                closeUpdate(stmt, conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new ArrayList<>();
        }
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

}

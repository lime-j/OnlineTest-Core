package com.onlinejudge.examservice;

import com.onlinejudge.util.DatabaseUtil;
import com.onlinejudge.util.ListEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.onlinejudge.util.DatabaseUtil.*;

public class ExamServiceListExam extends ListEvent {
    private String userID;
    private static Logger logger = LoggerFactory.getLogger(ExamServiceListExam.class);

    public ExamServiceListExam(String userID) {
        this.userID = userID;
    }

    public List<Exam> go() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseUtil.getConnection();
            logger.info("ExamServiceListExam, conn and stmt created.");
            //stmt.execute("use onlinejudge");
            String qry = String.format("select e.ename, e.eid, e.ename, e.estart, e.eend, e.etext, e.esubject, ep.sid from examperm ep, exam e where e.eid = ep.eid and ep.sid = '%s'", this.userID);
            stmt = prepareStatement(qry);
            logger.debug("ExamServiceListExam, qry = {}",qry);
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
                logger.debug(MessageFormat.format("ExamServiceListExam, ename = {0}, estart = {1},eend = {2},eID = {3},userID = {4}etext = {5}esubject = {6}", eTitle, eStartTime, eEndTime, eID, userID, eText, eSubject)
                );
            }
            closeQuery(queryResult, stmt, conn);
            logger.info(MessageFormat.format("[DBG]:ExamServiceListExam, find{0}result(s)", cnt));
            return resultList;
        } catch (SQLException sqlException) {
            logger.error(sqlException.getMessage(), sqlException);
            try {
                closeUpdate(stmt, conn);
            } catch (SQLException e) {
                logger.error(e.getMessage(),e);
            }
            return new ArrayList<>();
        }
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

}

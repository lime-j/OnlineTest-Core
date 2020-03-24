package com.onlinejudge.examservice;

import com.onlinejudge.util.ListEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static com.onlinejudge.util.DatabaseUtil.*;

public class ExamServiceListExamStudent extends ListEvent {
    public String examID;
    private static Logger logger = LoggerFactory.getLogger(ExamServiceListExamStudent.class);

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
            logger.info("conn and stmt settled.");
            //stmt.executeQuery("use onlinejudge");
            logger.debug("ExamServiceListExamStudent, qry = " + qry);
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
            logger.debug(MessageFormat.format("find{0}result(s)", cnt));
            return resultList;
        } catch (SQLException sqlException) {
            try {
                closeQuery(queryResult, stmt, conn);
            } catch (SQLException e) {
                logger.error(e.getMessage(),e);
            }
            logger.error(sqlException.getMessage(),sqlException);
            return new ArrayList<>();
        }
    }
}
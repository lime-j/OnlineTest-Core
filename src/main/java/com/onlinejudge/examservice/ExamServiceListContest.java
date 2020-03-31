package com.onlinejudge.examservice;

import com.onlinejudge.util.DatabaseUtil;
import com.onlinejudge.util.ListEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.onlinejudge.util.DatabaseUtil.*;

public class ExamServiceListContest implements ListEvent<Exam> {
    private final int type;
    private final String userID;
    private static final Logger logger = LoggerFactory.getLogger(ExamServiceListContest.class);

    public ExamServiceListContest(String userID, int type) {
        this.type = type;
        this.userID = userID;
    }

    @Override
    public void beforeGo() {
        // do nothing
    }

    @Override
    public void afterGo() {
        // do nothing
    }

    public List<Exam> go() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseUtil.getConnection();
            logger.info("ExamServiceListExam, conn and stmt created.");
            stmt = prepareStatement("select * from exam e");
            logger.debug("ExamServiceListExam, qry = {}", stmt);
            var queryResult = stmt.executeQuery();
            int cnt = 0;
            List<Exam> resultList = new ArrayList<>();
            while (queryResult.next()) {
                int isContest = queryResult.getInt("iscontest");
                logger.debug(isContest != 1 ? "course" : "contest");
                // contest is a special type of exam that could be listed
                assert (isContest == 1 || isContest == 0);
                if (isContest != type) continue;
                ++cnt;
                String eTitle = queryResult.getString("ename");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                var eStartTime = formatter.format(new java.util.Date(queryResult.getTimestamp("estart").getTime()));
                var eEndTime = formatter.format(new java.util.Date(queryResult.getTimestamp("eend").getTime()));
                var eID = queryResult.getString("eid");
                var eText = queryResult.getString("etext");
                resultList.add(new Exam(eID, eTitle, userID, eStartTime, eEndTime, eText));
                logger.debug("ExamServiceListExam, ename = {}, estart = {},eend = {},eID = {},userID = {},etext = {}", eTitle, eStartTime, eEndTime, eID, userID, eText);
            }
            closeQuery(queryResult, stmt, conn);
            logger.info("[DBG]:ExamServiceListExam, find{}result(s)", cnt);
            return resultList;
        } catch (SQLException sqlException) {
            logger.error(sqlException.getMessage(), sqlException);
            try {
                closeUpdate(stmt, conn);
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            }
            return new ArrayList<>();
        }
    }

}

package com.onlinejudge.examservice;

import com.alibaba.fastjson.JSONObject;
import com.onlinejudge.util.BooleanEvent;
import com.onlinejudge.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static com.alibaba.fastjson.JSON.parseObject;


public class ExamServiceAddUserIntoExam implements BooleanEvent {
    private final List<String> currentUserList;
    private final String examID;
    private static final Logger logger = LoggerFactory.getLogger(ExamServiceAddUserIntoExam.class);

    public ExamServiceAddUserIntoExam(List<String> origin, String examID) {
        this.currentUserList = origin;
        this.examID = examID;
    }

    @Override
    public void beforeGo() {
        // do nothing
    }

    @Override
    public void afterGo() {
        // do nothing
    }

    @Override
    public boolean go() {
        ResultSet queryResult = null;
        Connection conn = null;
        PreparedStatement staQuery = null;
        PreparedStatement staInsert = null;
        try {
            conn = DatabaseUtil.getConnection();
            staQuery = conn.prepareStatement("select * from examperm where eid= ? and sid= ? ;");
            for (String s : this.currentUserList) {
                JSONObject currJSONG = parseObject(s);

                staQuery.setString(1, this.examID);
                staQuery.setString(2, currJSONG.getString("uid"));

                // debug only
                logger.debug(staQuery.toString());
                //
                queryResult = staQuery.executeQuery();
                staInsert = conn.prepareStatement("insert into examperm (eid,sid) values (?, ?)");
                if (!queryResult.next()) {
                    staInsert.setString(1, this.examID);
                    staInsert.setString(2, currJSONG.getString("uid"));
                    logger.debug(staInsert.toString());
                    staInsert.executeUpdate();
                }
                queryResult.close();
                staInsert.close();
            }
            DatabaseUtil.closeQuery(queryResult, staQuery, conn);
        } catch (SQLException e) {
            logger.error("SQLException", e);
            return false;
        } finally {
            try {
                if (staInsert != null) staInsert.close();
                DatabaseUtil.closeQuery(queryResult, staQuery, conn);
            } catch (SQLException e) {
                logger.error("SQLException", e);
            }
        }
        return true;
    }
}

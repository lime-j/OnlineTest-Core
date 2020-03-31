package com.onlinejudge.manservice;

import com.alibaba.fastjson.JSONObject;
import com.onlinejudge.util.BooleanEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

import static com.onlinejudge.util.DatabaseUtil.*;

public class ManagerServiceTeacherSubject implements BooleanEvent {
    private final List<String> currTeacherSubject;
    private static final Logger logger = LoggerFactory.getLogger(ManagerServiceTeacherSubject.class);

    public ManagerServiceTeacherSubject(List<String> origionData) {
        this.currTeacherSubject = origionData;
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
        // 将教师执教科目插入到数据库
        // requestType: addTeacherSubject
        PreparedStatement staInsertTea = null;
        PreparedStatement staNewSubject = null;
        PreparedStatement staQuerySubject = null;
        PreparedStatement staQueryTea = null;
        ResultSet querySet = null;
        try {
            Connection conn = getConnection();
            staInsertTea = prepareStatement("insert into teasubject (subject, teacherid)" +
                    "values(?, ?)");
            staQuerySubject = prepareStatement("select * from subject where subject=?");
            staQueryTea = prepareStatement("select * from teasubject where subject=? and teacherid=?");

            for (String s : this.currTeacherSubject) {
                JSONObject currJSON = JSONObject.parseObject(s);
                String currSubject = currJSON.getString("Subject");
                String currTeacherID = currJSON.getString("teacherID");
                staQuerySubject.setString(1, currSubject);
                logger.info("Query Subject: " + staQuerySubject.toString());
                querySet = staQuerySubject.executeQuery();
                if (!querySet.next()) {
                    Objects.requireNonNull(staNewSubject).setString(1, currSubject);
                    logger.info("Insert new Subject: " + staNewSubject.toString());
                    staNewSubject.executeUpdate();
                }
                querySet.close();

                // 查询当前教师执教科目是否存在
                staQueryTea.setString(1, currSubject);
                staQueryTea.setString(2, currTeacherID);
                logger.info("Query Teacher and Subject: " + staQueryTea.toString());
                querySet = staQueryTea.executeQuery();
                if (!querySet.next()) {
                    staQueryTea.close();
                    staQueryTea = prepareStatement("select * from userinfo where uid=?");
                    staQueryTea.setString(1, currTeacherID);
                    logger.info(MessageFormat.format("{0}: {1}", this.toString(), staQueryTea.toString()));
                    querySet = staQueryTea.executeQuery();
                    if (querySet.next()) {
                        staInsertTea.setString(1, currSubject);
                        staInsertTea.setString(2, currTeacherID);
                        logger.info("[ManagerService]: Insert into TeaSubject: " + staInsertTea.toString());
                        staInsertTea.executeUpdate();
                    }
                }

            }

        } catch (SQLException e) {
            logger.info(e.getMessage(), e);
            return false;
        } finally {
            try {
                Objects.requireNonNull(querySet).close();
                Objects.requireNonNull(staInsertTea).close();
                Objects.requireNonNull(staNewSubject).close();
                Objects.requireNonNull(staQuerySubject).close();
                Objects.requireNonNull(staQueryTea).close();
            } catch (SQLException e) {
                logger.info(e.getMessage(), e);
            }
            closeConnection();
        }
        return true;
    }
}

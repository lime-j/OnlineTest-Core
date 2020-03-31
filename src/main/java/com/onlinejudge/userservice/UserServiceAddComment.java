package com.onlinejudge.userservice;

import com.onlinejudge.util.BooleanEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

import static com.onlinejudge.util.DatabaseUtil.closeUpdate;
import static com.onlinejudge.util.DatabaseUtil.prepareStatement;

@Log4j2
@Getter
@Setter
public class UserServiceAddComment implements BooleanEvent {
    private final String examID;
    private final String userID;
    private final String text;
    private static final int TIMELINE_COMMENT = 3;
    private PreparedStatement stmt = null;
    private ResultSet ret = null;

    public UserServiceAddComment(String userID, String examID, String text) {
        this.examID = examID;
        this.text = text;
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

    private void setComment() throws SQLException {
        stmt = prepareStatement("insert into comment values(?,?,?,?,?)");
        stmt.setString(1, text);
        stmt.setString(2, UUID.randomUUID().toString().replace("-", ""));
        stmt.setString(3, userID);
        stmt.setString(4, examID);
        stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
        log.debug(stmt);
        stmt.close();
    }

    private String getExamName() throws SQLException {
        String examName = null;
        stmt = prepareStatement("select ename from exam where eid = ?");
        stmt.setString(1, examID);
        ret = stmt.executeQuery();
        log.debug(stmt);
        int count = 0;
        while (ret.next()) {
            ++count;
            examName = ret.getString("ename");
        }
        assert count == 1;
        stmt.close();
        ret.close();
        return examName;
    }

    private void setTimelineComment(String examName) throws SQLException {
        stmt = prepareStatement("insert into timeline values(?,?,?,?,?)");
        stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
        stmt.setInt(2, TIMELINE_COMMENT);
        stmt.setString(3, userID);
        stmt.setString(4, examName);
        stmt.setString(5, text);
        log.debug(stmt);
        stmt.executeUpdate();
        closeUpdate(stmt);
    }

    public boolean go() {
        try {
            setComment();
            setTimelineComment(getExamName());
            return true;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            try {
                closeUpdate(stmt);
            } catch (SQLException ex) {
                log.error(ex.getMessage(), ex);
            }
            return false;
        }
    }
}

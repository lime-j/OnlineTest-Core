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

import static com.onlinejudge.userservice.UserServiceGetExamName.getExamName;
import static com.onlinejudge.util.DatabaseUtil.closeUpdate;
import static com.onlinejudge.util.DatabaseUtil.prepareStatement;

@Log4j2
@Getter
@Setter
public class UserServiceAddComment implements BooleanEvent {
    private final String examID;
    private final String userID;
    private final String text;
    private final String facID;
    private final String userName;
    private static final int TIMELINE_COMMENT = 3;
    private PreparedStatement stmt = null;
    private ResultSet ret = null;

    public UserServiceAddComment(String userID, String examID, String text, String facID, String userName) {
        this.examID = examID;
        this.text = text;
        this.userID = userID;
        this.facID = facID;
        this.userName = userName;
    }

    @Override
    public void beforeGo() {
        // do nothing
    }

    @Override
    public void afterGo() {
        UserServiceSetTimeline.setItem(
                new TimelineItem(getExamName(examID),
                        text,
                        3,
                        userID,
                        new Timestamp(System.currentTimeMillis())));

    }

    private void setComment() throws SQLException {
        if (facID == null) {
            stmt = prepareStatement("insert into comment(ctext, cid, uid, eid, time, uname) values(?,?,?,?,?)");
        } else {
            stmt = prepareStatement("insert into comment(ctext, cid, uid, eid, time, uname, facid) values(?,?,?,?,?,?,?)");
            stmt.setString(6, facID);
        }
        stmt.setString(1, text);
        stmt.setString(2, UUID.randomUUID().toString().replace("-", ""));
        stmt.setString(3, userID);
        stmt.setString(4, examID);
        stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
        log.debug(stmt);
        stmt.close();
    }


    public boolean go() {
        try {
            setComment();
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

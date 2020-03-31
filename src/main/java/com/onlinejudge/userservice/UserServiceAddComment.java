package com.onlinejudge.userservice;

import com.onlinejudge.util.BooleanEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

import static com.onlinejudge.util.DatabaseUtil.*;

@Log4j2
@Getter
@Setter
public class UserServiceAddComment extends BooleanEvent {
    private final String examID;
    private final String userID;
    private final String text;

    public UserServiceAddComment(String userID, String examID, String text) {
        this.examID = examID;
        this.text = text;
        this.userID = userID;
    }

    public boolean go() {
        getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = prepareStatement("insert into comment values(?,?,?,?,?)");
            stmt.setString(1, text);
            stmt.setString(2, UUID.randomUUID().toString().replace("-", ""));
            stmt.setString(3, userID);
            stmt.setString(4, examID);
            stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            log.debug(stmt);
            stmt.executeUpdate();
            closeUpdate(stmt);
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

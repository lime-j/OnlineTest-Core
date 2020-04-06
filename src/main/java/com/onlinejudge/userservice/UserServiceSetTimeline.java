package com.onlinejudge.userservice;

import com.onlinejudge.util.Settler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.onlinejudge.util.DatabaseUtil.closeUpdate;
import static com.onlinejudge.util.DatabaseUtil.prepareStatement;


public class UserServiceSetTimeline implements Settler {
    private static final Logger log = LoggerFactory.getLogger(UserServiceSetTimeline.class);
    public static void setItem(TimelineItem t) {
        PreparedStatement stmt = null;
        try {
            stmt = prepareStatement("insert into timeline values(?,?,?,?,?)");
            stmt.setTimestamp(1, t.getTime());
            stmt.setInt(2, t.getType());
            stmt.setString(3, t.getUid());
            stmt.setString(4, t.getName());
            stmt.setString(5, t.getDescription());
            log.debug(String.valueOf(stmt));
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                closeUpdate(stmt);
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}

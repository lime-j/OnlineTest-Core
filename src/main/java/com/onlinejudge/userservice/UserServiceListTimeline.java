package com.onlinejudge.userservice;

import com.onlinejudge.util.ListEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.onlinejudge.util.DatabaseUtil.closeQuery;
import static com.onlinejudge.util.DatabaseUtil.prepareStatement;


public class UserServiceListTimeline implements ListEvent<SendableTimelineItem> {
    private static final int COMMENT_MAX_LENGTH = 19;
    private final String userID;
    private static final Logger log = LoggerFactory.getLogger(UserServiceListTimeline.class);
    public UserServiceListTimeline(@NotNull String userID) {
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

    @Override
    public List<SendableTimelineItem> go() {
        PreparedStatement stmt = null;
        ResultSet ret = null;
        List<SendableTimelineItem> result = new ArrayList<>();
        try {
            stmt = prepareStatement("select * from timeline where uid = ?");
            stmt.setString(1, userID);
            log.debug(String.valueOf(stmt));
            ret = stmt.executeQuery();
            while (ret.next()) {
                String name = ret.getString("name");
                String description = ret.getString("description");
                Timestamp ts = ret.getTimestamp("time");
                int type = ret.getInt("type");
                if (description.length() >= 20) {
                    description = description.substring(0, COMMENT_MAX_LENGTH) + "...";
                }
                result.add(new SendableTimelineItem(new TimelineItem(name, description, type, userID, ts)));
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                closeQuery(ret, stmt);
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }
        return result;
    }
}

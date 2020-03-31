package com.onlinejudge.userservice;

import com.onlinejudge.util.ListEvent;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.onlinejudge.util.DatabaseUtil.closeQuery;
import static com.onlinejudge.util.DatabaseUtil.prepareStatement;

@Log4j2
public class UserServiceListTimeline implements ListEvent<SendableTimelineItem> {
    private static final int COMMENT_MAX_LENGTH = 19;
    private final String userID;

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
            log.debug(stmt);
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

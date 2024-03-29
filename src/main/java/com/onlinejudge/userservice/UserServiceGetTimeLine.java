package com.onlinejudge.userservice;

import com.onlinejudge.util.DatabaseUtil;
import com.onlinejudge.util.InternalException;
import com.onlinejudge.util.ListEvent;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
public class UserServiceGetTimeLine implements ListEvent<SendableTimelineItem> {
    private String userID;
    private static final Logger log = LoggerFactory.getLogger(UserServiceGetTimeLine.class);
    @Override
    public void beforeGo() {
        // do nothing
    }

    @Override
    public void afterGo() {
        // do nothing
    }

    @Override
    public List<SendableTimelineItem> go() throws InternalException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet res = null;
        List<SendableTimelineItem> result = new ArrayList<>();
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("select * from timeline where uid = ?");
            stmt.setString(1, userID);
            res = stmt.executeQuery();
            while (res.next()) {
                var time = res.getTimestamp("time");
                var type = res.getInt("type");
                var name = res.getString("name");
                var description = res.getString("description");
                var timelineItem = new TimelineItem(name, description, type, userID, time);
                result.add(new SendableTimelineItem(timelineItem));
            }
            return result;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                DatabaseUtil.closeQuery(res, stmt, conn);
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }
        return new ArrayList<>();
    }
}

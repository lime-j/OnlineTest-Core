package com.onlinejudge.examservice;

import com.onlinejudge.userservice.TimelineItem;
import com.onlinejudge.userservice.UserServiceSetTimeline;
import com.onlinejudge.util.BooleanEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import static com.onlinejudge.userservice.UserServiceGetExamName.getExamName;
import static com.onlinejudge.util.DatabaseUtil.closeUpdate;
import static com.onlinejudge.util.DatabaseUtil.prepareStatement;


public class ExamServiceJoinContest implements BooleanEvent {
    private static final Logger log = LoggerFactory.getLogger(ExamServiceJoinContest.class);
    private String userID;
    private String examID;
    private int isContest;

    public ExamServiceJoinContest(@NotNull String userID, @NotNull String examID) {
        this.userID = userID;
        this.examID = examID;
        this.isContest = 0;
    }

    public ExamServiceJoinContest(@NotNull String userID, @NotNull String examID, int isContest) {
        this.userID = userID;
        this.examID = examID;
        this.isContest = isContest;
    }

    @Override
    public boolean go() {
        PreparedStatement stmt = null;
        boolean result = true;
        try {
            stmt = prepareStatement("insert into examperm (eid, sid) values(?,?)");
            stmt.setString(1, examID);
            stmt.setString(2, userID);
            stmt.executeUpdate();
            log.debug(String.valueOf(stmt));
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            result = false;
        } finally {
            try {
                closeUpdate(stmt);
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }
        return result;
    }

    @Override
    public void beforeGo() {
        // DO NOTHING
    }

    @Override
    public void afterGo() {
        if (isContest != 0) return;
        PreparedStatement stmt = null;
        try {
            stmt = prepareStatement("update userinfo set usex = usex + 1 where uid = ?");
            stmt.setString(1, userID);
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
        UserServiceSetTimeline.setItem(new TimelineItem(
                getExamName(examID), " ", 2, userID, new Timestamp(System.currentTimeMillis())));
    }
}

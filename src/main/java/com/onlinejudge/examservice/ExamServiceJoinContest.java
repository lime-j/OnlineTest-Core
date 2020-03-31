package com.onlinejudge.examservice;

import com.onlinejudge.userservice.TimelineItem;
import com.onlinejudge.userservice.UserServiceSetTimeline;
import com.onlinejudge.util.BooleanEvent;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import static com.onlinejudge.userservice.UserServiceGetExamName.getExamName;
import static com.onlinejudge.util.DatabaseUtil.closeUpdate;
import static com.onlinejudge.util.DatabaseUtil.prepareStatement;

@Log4j2
public class ExamServiceJoinContest implements BooleanEvent {
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
            log.debug(stmt);
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

        UserServiceSetTimeline.setItem(new TimelineItem(
                getExamName(examID), " ", 2, userID, new Timestamp(System.currentTimeMillis())));
    }
}

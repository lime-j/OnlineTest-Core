package com.onlinejudge.problemservice;

import com.onlinejudge.util.BooleanEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.onlinejudge.util.DatabaseUtil.*;

public class ProblemServiceChangeScore extends BooleanEvent {
    private final String Sid;
    private final int Score;
    private static final Logger logger = LoggerFactory.getLogger(ProblemServiceChangeScore.class);

    public ProblemServiceChangeScore(String Sid, int Score) {
        this.Sid = Sid;
        this.Score = Score;
    }

    @Override
    public boolean go() {
        try {
            Connection conn = getConnection();
            PreparedStatement sta;
            sta = prepareStatement("update submission " +
                    "set sscore=?,sjudged=1 where sid=?");
            sta.setInt(1, this.Score);
            sta.setString(2, this.Sid);
            logger.debug("[problemservice]: ChangeScore: SQL: " + sta.toString());
            sta.executeUpdate();
            closeUpdate(sta, conn);
            logger.debug(String.format("problemservice: Subjective Score update - \n\tsid=%s score=%d", this.Sid, this.Score));
        } catch (SQLException e) {
            logger.error(e.getMessage(),e);
            return false;
        }
        return true;

    }
}

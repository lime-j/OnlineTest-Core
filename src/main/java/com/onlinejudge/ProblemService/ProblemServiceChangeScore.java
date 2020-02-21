package com.onlinejudge.ProblemService;

import com.onlinejudge.util.BooleanEvent;

import java.sql.*;

import static com.onlinejudge.DaemonService.DaemonServiceMain.*;
import static com.onlinejudge.util.DatabaseUtil.*;

public class ProblemServiceChangeScore extends BooleanEvent {
    private String Sid;
    private int Score;

    public ProblemServiceChangeScore(String Sid, int Score) {
        this.Sid = Sid;
        this.Score = Score;
    }

    @Override
    public boolean go() {
        try {
            Connection conn = getConnection();
            PreparedStatement sta = null;
            sta = prepareStatement("update submission " +
                    "set sscore=?,sjudged=1 where sid=?");
            sta.setInt(1, this.Score);
            sta.setString(2, this.Sid);
            debugPrint("[ProblemService]: ChangeScore: SQL: " + sta.toString());
            sta.executeUpdate();
            closeUpdate(sta, conn);
            debugPrint(String.format("ProblemService: Subjective Score update - \n\tsid=%s score=%d", this.Sid, this.Score));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }
}

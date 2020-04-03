package com.onlinejudge.predictservice;

import com.onlinejudge.util.BooleanEvent;
import com.onlinejudge.util.DatabaseUtil;
import lombok.extern.log4j.Log4j2;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.onlinejudge.util.DatabaseUtil.closeConnection;
import static com.onlinejudge.util.DatabaseUtil.prepareStatement;

@Log4j2
public class PredictServiceAddUserStar implements BooleanEvent {
    protected static final Map<String, Integer> examIDMapping = new HashMap<>();

    static {
        PreparedStatement stmt = null;
        ResultSet ret = null;
        try {
            stmt = DatabaseUtil.prepareStatement("select eid from exam");
            ret = stmt.executeQuery();
            int cnt = 0;
            while (ret.next()) {
                String eid = ret.getString(1);
                if (examIDMapping.get(eid) == null) {
                    examIDMapping.put(eid, cnt);
                    ++cnt;
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (ret != null) {
                    ret.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
            closeConnection();
        }
    }

    private final String examID;
    private final String userID;
    private int isInteresting;
    private int isChallenging;
    private int userRating;

    public PredictServiceAddUserStar(String examID, String userID, int isChallenging, int isInteresting, int userRating) {
        this.userID = userID;
        this.userRating = userRating;
        this.isChallenging = isChallenging;
        this.isInteresting = isInteresting;
        this.examID = examID;
    }

    protected static boolean[] handle(String userID) {
        PreparedStatement stmt = null;
        ResultSet ret = null;
        boolean[] bytes = new boolean[UserStar.MAXN];
        try {
            stmt = prepareStatement("select eid from examperm where sid = ?");
            stmt.setString(1, userID);
            ret = stmt.executeQuery();
            while (ret.next()) {
                var tmp = examIDMapping.get(ret.getString(1));
                if (tmp != null) bytes[tmp] = true;
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (ret != null) ret.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }
        return bytes;
    }

    @Override
    public boolean go() {
        PreparedStatement stmt = null;
        ResultSet ret = null;
        try {
            boolean[] bytes = handle(userID);
            ret.close();
            stmt.close();
            stmt = prepareStatement("insert into user_star(favor, difficulty,uid, urating,eid, coursestring) values (?,?,?,?,?,?)");
            stmt.setInt(1, isInteresting);
            stmt.setInt(2, isChallenging);
            stmt.setString(3, userID);
            stmt.setInt(4, userRating);
            stmt.setString(5, examID);
            stmt.setString(6, Arrays.toString(bytes));
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        } finally {

        }
        return true;
    }

    @Override
    public void beforeGo() {
        // do nothing
    }

    @Override
    public void afterGo() {
        // do nothing
    }
}

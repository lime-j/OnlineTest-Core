package com.onlinejudge.predictservice;

import com.onlinejudge.util.Provider;
import lombok.extern.log4j.Log4j2;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.onlinejudge.util.DatabaseUtil.closeQuery;
import static com.onlinejudge.util.DatabaseUtil.prepareStatement;

@Log4j2
public class PredictServiceGetUserStar implements Provider {

    public static List<UserStar> getItem(String examID) {
        PreparedStatement stmt = null;
        ResultSet ret = null;
        List<UserStar> result = new ArrayList<>();
        try {
            stmt = prepareStatement("select * from user_star where eid = ?");
            stmt.setString(1, examID);
            ret = stmt.executeQuery();
            while (ret.next()) {
                String userID = ret.getString("uid");
                int userRating = ret.getInt("urating");
                int isInteresting = ret.getInt("favor");
                int isChallenging = ret.getInt("difficulty");
                boolean[] studiedCources = new boolean[128];
                String tmp = ret.getString("coursestring");
                int len = tmp.length();
                for (int i = 0; i < len; ++i) {
                    if (tmp.charAt(i) == '1') studiedCources[i] = true;
                }
                result.add(new UserStar(userID, examID, userRating, isInteresting, isChallenging, studiedCources));
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

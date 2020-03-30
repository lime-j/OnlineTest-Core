package com.onlinejudge.examservice;

import com.onlinejudge.examservice.ExamServiceGetRating.Participant;
import com.onlinejudge.util.Provider;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static com.onlinejudge.util.DatabaseUtil.*;

@Log4j2
public class ExamServiceGetParticipants implements Provider {
    private static List<>

    public static List<Participant> getItem(String examID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet ret = null;
        try {
            conn = getConnection();
            stmt = prepareStatement("select sid, utype as old_rating,`rank` from examperm ex, userinfo ui where ex.sid = ui.uid and ex.eid = ?");
            stmt.setString(1, examID);
            ret = stmt.executeQuery();
            while (ret.next()) {
                ret.getString("sid");
                ret.getInt("rating_delta")
            }
        } catch (SQLException e) {

        } finally {
            try {
                closeQuery(ret, stmt, conn);
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}

package com.onlinejudge.examservice;

import com.onlinejudge.examservice.ExamServiceGetRating.Participant;
import com.onlinejudge.util.InternalException;
import com.onlinejudge.util.Provider;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.onlinejudge.util.DatabaseUtil.*;

@Log4j2
public class ExamServiceGetParticipants implements Provider {
    private ExamServiceGetParticipants() {
    }

    @NotNull
    private static List<Participant> getRankedParticipants(String examID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet ret = null;
        List<Participant> pans = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = prepareStatement("select uname, sid, utype as old_rating,`rank` from examperm ex, userinfo ui where ex.sid = ui.uid and ex.eid = ?");
            stmt.setString(1, examID);
            log.debug(stmt);
            ret = stmt.executeQuery();
            while (ret.next()) {
                String sid = ret.getString("sid");
                int oldRating = ret.getInt("old_rating");
                int rank = ret.getInt("rank");
                String sname = ret.getString("uname");
                pans.add(new Participant(rank, sname, sid, oldRating, 0, 0, 0));
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                closeQuery(ret, stmt, conn);
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }
        return pans;
    }

    @NotNull
    public static List<Participant> getItem(String examID) throws InternalException {
        return getRankedParticipants(examID);
    }

}

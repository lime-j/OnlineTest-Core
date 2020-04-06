package com.onlinejudge.examservice;

import com.onlinejudge.examservice.ExamServiceGetRating.Participant;
import com.onlinejudge.util.Settler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.onlinejudge.util.DatabaseUtil.closeConnection;
import static com.onlinejudge.util.DatabaseUtil.prepareStatement;


public class ExamServiceSetRating implements Settler {
    private static final Logger log = LoggerFactory.getLogger(ExamServiceSetRating.class);
    public static boolean setItem(@NotNull List<Participant> pans, String examID) {
        PreparedStatement stmtExamperm = null;
        PreparedStatement stmtUserinfo = null;
        boolean ret = true;
        try {
            stmtExamperm = prepareStatement("update examperm set rating_delta = ? and rank = ? where eid = ? and sid = ?");
            stmtUserinfo = prepareStatement("update userinfor set utype = ? where uid = ?");
            stmtExamperm.setString(3, examID);
            for (var pan : pans) {
                stmtExamperm.setInt(2, (int) pan.rank);
                stmtUserinfo.setString(2, pan.userID);
                stmtExamperm.setInt(1, pan.delta);
                stmtExamperm.setString(4, pan.userID);
                stmtUserinfo.setInt(1, pan.newRating);
                log.debug(String.valueOf(stmtExamperm));
                log.debug(String.valueOf(stmtUserinfo));
                stmtExamperm.executeUpdate();
                stmtUserinfo.executeUpdate();
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ret = false;
        } finally {
            try {
                if (stmtExamperm != null) stmtExamperm.close();
                if (stmtUserinfo != null) stmtUserinfo.close();
                closeConnection();
            } catch (SQLException ee) {
                log.error(ee.getMessage(), ee);
            }
        }
        return ret;
    }
}

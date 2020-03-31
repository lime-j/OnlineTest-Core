package com.onlinejudge.examservice;

import com.onlinejudge.util.Settler;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.onlinejudge.util.DatabaseUtil.*;

@Log4j2
public class ExamServiceSetRank implements Settler {

    public static void setItem(List<RankedUser> pans, String examID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = prepareStatement("update examperm set rank = ? where sid = ? and eid = ?");
            stmt.setString(3, examID);
            for (var pan : pans) {
                stmt.setString(1, Integer.toString(pan.getRank()));
                stmt.setString(2, pan.getUserID());
                log.debug(stmt);
                int status = stmt.executeUpdate();
                log.info("status = {}", status);
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                closeUpdate(stmt, conn);
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}

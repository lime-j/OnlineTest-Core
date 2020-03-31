package com.onlinejudge.examservice;

import com.onlinejudge.util.InternalException;
import com.onlinejudge.util.Provider;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import static com.onlinejudge.util.DatabaseUtil.closeQuery;
import static com.onlinejudge.util.DatabaseUtil.getConnection;
import static com.onlinejudge.util.DatabaseUtil.prepareStatement;

@Log4j2
public class ExamServiceGetContestTime implements Provider {
    @NotNull
    @Contract("_ -> new")
    public static Pair<Timestamp, Timestamp> getItem(String examID) throws InternalException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            conn = getConnection();
            stmt = prepareStatement("select estart, eend, eid from exam where eid = ?");
            stmt.setString(1, examID);
            log.debug(stmt);
            res = stmt.executeQuery();
            int cnt = 0;
            Timestamp start = null;
            Timestamp end = null;
            while (res.next()) {
                ++cnt;
                start = res.getTimestamp("estart");
                end = res.getTimestamp("eend");
            }
            if (start == null || end == null)
                throw new InternalException("start or end == null, database may has been broken");
            if (cnt != 1) throw new InternalException("There is no such exam.");
            closeQuery(res, stmt, conn);
            return new ImmutablePair<>(start, end);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            try {
                closeQuery(res, stmt, conn);
            } catch (SQLException ex) {
                log.error(ex.getMessage(), ex);
            }
            throw new InternalException("SQL Error");
        }
    }
}

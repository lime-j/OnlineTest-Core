package com.onlinejudge.examservice;

import com.onlinejudge.util.InternalException;
import com.onlinejudge.util.Provider;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

import static com.onlinejudge.util.DatabaseUtil.*;


public class ExamServiceGetContestTime implements Provider {
    private static final Logger log = LoggerFactory.getLogger(ExamServiceGetContestTime.class);
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
            log.debug(String.valueOf(stmt));
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

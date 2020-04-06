package com.onlinejudge.userservice;

import com.onlinejudge.util.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.onlinejudge.util.DatabaseUtil.prepareStatement;


public class UserServiceGetExamName implements Provider {
    private static final Logger log = LoggerFactory.getLogger(UserServiceGetExamName.class);
    public static String getExamName(String examID) {
        String examName = null;
        PreparedStatement stmt = null;
        ResultSet ret = null;
        try {
            stmt = prepareStatement("select ename from exam where eid = ?");
            stmt.setString(1, examID);
            ret = stmt.executeQuery();
            log.debug(String.valueOf(stmt));
            int count = 0;
            while (ret.next()) {
                ++count;
                examName = ret.getString("ename");
            }
            assert count == 1;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (ret != null)
                    ret.close();
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }

        }
        return examName;
    }

}

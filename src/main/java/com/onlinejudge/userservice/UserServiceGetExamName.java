package com.onlinejudge.userservice;

import com.onlinejudge.util.Provider;
import lombok.extern.log4j.Log4j2;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.onlinejudge.util.DatabaseUtil.prepareStatement;

@Log4j2
public class UserServiceGetExamName implements Provider {
    public static String getExamName(String examID) {
        String examName = null;
        PreparedStatement stmt = null;
        ResultSet ret = null;
        try {
            prepareStatement("select ename from exam where eid = ?");
            stmt.setString(1, examID);
            ret = stmt.executeQuery();
            log.debug(stmt);
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

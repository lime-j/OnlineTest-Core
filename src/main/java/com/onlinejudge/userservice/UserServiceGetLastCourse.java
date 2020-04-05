package com.onlinejudge.userservice;

import com.onlinejudge.util.StringEvent;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.onlinejudge.util.DatabaseUtil.closeQuery;
import static com.onlinejudge.util.DatabaseUtil.prepareStatement;

@Log4j2
public class UserServiceGetLastCourse implements StringEvent {
    private final String userID;

    public UserServiceGetLastCourse(@NotNull String userID) {
        this.userID = userID;
    }

    @Override
    public String go() {
        PreparedStatement stmt = null;
        ResultSet ret = null;
        String result = null;
        try {
            stmt = prepareStatement("select e.eid as cid,e.ename as cname from examperm ep, exam e where ep.sid = ? and ep.eid = e.eid and e.iscontest=0 and ep.rank=0 order by time desc");
            stmt.setString(1, userID);
            ret = stmt.executeQuery();
            if (ret.next()) {
                result = ret.getString("cid");
                result = result + "@" + ret.getString("cname");
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

    @Override
    public void beforeGo() {
        // do nothing
    }

    @Override
    public void afterGo() {
        // do nothing
    }
}

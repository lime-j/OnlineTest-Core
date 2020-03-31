package com.onlinejudge.userservice;

import com.onlinejudge.util.DatabaseUtil;
import com.onlinejudge.util.ListEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.onlinejudge.util.DatabaseUtil.closeQuery;

@Getter
@Setter
@Log4j2
@AllArgsConstructor
public class UserServiceListComment implements ListEvent<Comment> {
    private final String examID;

    @Override
    public void beforeGo() {
        // do nothing
    }

    @Override
    public void afterGo() {
        // do nothing
    }

    public List<Comment> go() {
        PreparedStatement stmt = null;
        ResultSet ret = null;
        List<Comment> result = new ArrayList<>();
        try {
            stmt = DatabaseUtil.prepareStatement("select * from comment where eid = ?");
            stmt.setString(1, examID);
            log.debug(stmt);
            ret = stmt.executeQuery();
            while (ret.next()) {
                var userID = ret.getString("uid");
                var time = ret.getTimestamp("time");
                var ctext = ret.getString("ctext");
                result.add(new Comment(userID, ctext, time));
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

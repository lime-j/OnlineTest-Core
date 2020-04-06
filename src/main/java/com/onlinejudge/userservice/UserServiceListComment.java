package com.onlinejudge.userservice;

import com.onlinejudge.util.ListEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.onlinejudge.util.DatabaseUtil.closeQuery;
import static com.onlinejudge.util.DatabaseUtil.prepareStatement;

@Getter
@Setter
@Log4j2
@AllArgsConstructor
public class UserServiceListComment implements ListEvent<CommentDetailBean> {
    private final String examID;

    @Override
    public void beforeGo() {
        // do nothing
    }

    @Override
    public void afterGo() {
        // do nothing
    }

    public List<CommentDetailBean> go() {
        PreparedStatement stmt = null;
        ResultSet ret = null;
        List<CommentDetailBean> result = new ArrayList<>();
        try {
            stmt = prepareStatement("select * from comment where eid = ? and facid is not NULL");
            List<CommentDetailBean> faidLst = new ArrayList<>();
            Map<String, List<ReplyDetailBean>> commentLstMap = new HashMap<>();
            stmt.setString(1, examID);
            log.debug(stmt);
            ret = stmt.executeQuery();
            while (ret.next()) {
                var userID = ret.getString("uid");
                var time = ret.getTimestamp("time");
                var ctext = ret.getString("ctext");
                var uname = ret.getString("uname");
                var cid = ret.getString("cid");
                List<ReplyDetailBean> replyDetailBeans = new ArrayList<>();
                faidLst.add(new CommentDetailBean(cid, userID, ctext, time, uname, replyDetailBeans));
                commentLstMap.put(cid, replyDetailBeans);
            }
            ret.close();
            stmt.close();
            stmt = prepareStatement("select * from comment where eid = ? and facid is NULL");
            stmt.setString(1, examID);
            log.debug(stmt);
            ret = stmt.executeQuery();
            while (ret.next()) {
                var userID = ret.getString("uid");
                var time = ret.getTimestamp("time");
                var ctext = ret.getString("ctext");
                var uname = ret.getString("uname");
                var cid = ret.getString("cid");
                commentLstMap.get(cid).add(new ReplyDetailBean(uname, userID, ctext, time, cid));
            }
            log.debug(faidLst);
            return faidLst;
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

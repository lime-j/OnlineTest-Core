package com.onlinejudge.userservice;

import com.onlinejudge.util.ListEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

@AllArgsConstructor
public class UserServiceListComment implements ListEvent<CommentDetailBean> {
    private final String examID;
    private static final Logger log = LoggerFactory.getLogger(UserServiceListComment.class);
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
            stmt = prepareStatement("select * from comment where eid = ? and facid is NULL");
            List<CommentDetailBean> faidLst = new ArrayList<>();
            Map<String, List<ReplyDetailBean>> commentLstMap = new HashMap<>();
            stmt.setString(1, examID);
            log.debug(String.valueOf(stmt));
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
            stmt = prepareStatement("select * from comment where eid = ? and facid is not NULL");
            stmt.setString(1, examID);
            log.debug(String.valueOf(stmt));
            ret = stmt.executeQuery();
            while (ret.next()) {
                var userID = ret.getString("uid");
                var time = ret.getTimestamp("time");
                var ctext = ret.getString("ctext");
                var uname = ret.getString("uname");
                var cid = ret.getString("cid");
                var facid = ret.getString("facid");
                commentLstMap.get(facid).add(new ReplyDetailBean(uname, userID, ctext, time, cid));
            }
            log.debug(String.valueOf(faidLst));
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

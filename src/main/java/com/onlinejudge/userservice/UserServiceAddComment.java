package com.onlinejudge.userservice;

import com.alibaba.fastjson.JSONObject;
import com.onlinejudge.util.ClassEvent;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static com.onlinejudge.userservice.UserServiceGetExamName.getExamName;
import static com.onlinejudge.util.DatabaseUtil.closeUpdate;
import static com.onlinejudge.util.DatabaseUtil.prepareStatement;


@Getter
@Setter
public class UserServiceAddComment implements ClassEvent {
    private final String examID;
    private final String userID;
    private final String text;
    private final String facID;
    private final String userName;
    private static final int TIMELINE_COMMENT = 3;
    private PreparedStatement stmt = null;
    private ResultSet ret = null;
    private static final Logger log = LoggerFactory.getLogger(UserServiceAddComment.class);
    public UserServiceAddComment(String userID, String examID, String text, String facID, String userName) {
        this.examID = examID;
        this.text = text;
        this.userID = userID;
        this.facID = facID;
        this.userName = userName;
    }

    @Override
    public void beforeGo() {
        // do nothing
    }

    @Override
    public void afterGo() {
        UserServiceSetTimeline.setItem(
                new TimelineItem(getExamName(examID),
                        text,
                        3,
                        userID,
                        new Timestamp(System.currentTimeMillis())));

    }

    private AddCommentResult setComment() throws SQLException {
        if (facID == null) {
            stmt = prepareStatement("insert into comment(ctext, cid, uid, eid, time, uname) values(?,?,?,?,?,?)");
        } else {
            stmt = prepareStatement("insert into comment(ctext, cid, uid, eid, time, uname, facid) values(?,?,?,?,?,?,?)");
            stmt.setString(7, facID);
        }
        String commentID = UUID.randomUUID().toString().replace("-", "");
        Timestamp time = new Timestamp(System.currentTimeMillis());
        stmt.setString(1, text);
        stmt.setString(2, commentID);
        stmt.setString(3, userID);
        stmt.setString(4, examID);
        stmt.setTimestamp(5, time);
        stmt.setString(6,userName);
        stmt.executeUpdate();
        log.debug(String.valueOf(stmt));
        stmt.close();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return new AddCommentResult(formatter.format((new Date(time.getTime()))), commentID);
    }


    public String go() {
        try {
            return JSONObject.toJSONString(setComment());
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            try {
                closeUpdate(stmt);
            } catch (SQLException ex) {
                log.error(ex.getMessage(), ex);
            }
            return "";
        }
    }
}

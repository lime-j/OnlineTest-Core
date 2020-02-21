package com.onlinejudge.ProblemService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.onlinejudge.DaemonService.DaemonServiceMain.debugPrint;
import static com.onlinejudge.util.DatabaseUtil.*;

public class Tag {
    // 添加新知识点
    // requestType:
    private String subject, tagName;

    public Tag(String subject, String tagName) {
        this.subject = subject;
        this.tagName = tagName;
    }

    public boolean updateTag() {
        try {
            Connection conn = getConnection();
            PreparedStatement staQueryTag = prepareStatement("select * from subjecttag where subject=? and tag=?");
            PreparedStatement staUpdateTag = prepareStatement("insert into subjecttag (subject, tag) values (?, ?)");
            staQueryTag.setString(1, this.subject);
            staQueryTag.setString(2, this.tagName);
            debugPrint("[ProblemService]: " + this.toString() + ": " + staQueryTag.toString());
            var queryResult = staQueryTag.executeQuery();
            if (!queryResult.next()) {
                staUpdateTag.setString(1, this.subject);
                staUpdateTag.setString(2, this.tagName);
                debugPrint("[ProblemService]: " + this.toString() + ": " + staUpdateTag.toString());
                staUpdateTag.executeUpdate();
            }
            staQueryTag.close();
            staUpdateTag.close();
            closeConnection();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

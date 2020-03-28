package com.onlinejudge.problemservice;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.onlinejudge.util.DatabaseUtil.*;

public class Submission {
    private String Sid;
    private final String SubText;
    private String SubTime;
    private final String SubUser;
    private final String SubProb;
    private String SubExam;
    private int SubScore;

    private static final Logger logger = LoggerFactory.getLogger(Submission.class);


    @Contract(pure = true)
    public Submission(String SubText, String SubUser, String SubProb, String SubExam, int SubScore) {
        this.Sid = "";
        this.SubText = SubText;
        this.SubUser = SubUser;
        this.SubProb = SubProb;
        this.SubExam = SubExam;
        this.SubScore = SubScore;
    }

    @Contract(pure = true)
    public Submission(String SubText, String SubUser, String SubProb, String SubExam) {
        // DeamonServiceRunnable重载
        // 提交到服务器的重载
        this.Sid = "";
        this.SubText = SubText;
        this.SubUser = SubUser;
        this.SubProb = SubProb;
        this.SubExam = SubExam;
    }

    @Contract(pure = true)
    public Submission(String Sid, String SubText, String SubTime, String SubUser, String SubProb) {
        // ObjectiveSubmission重载用
        this.Sid = Sid;
        this.SubText = SubText;
        this.SubTime = SubTime;
        this.SubUser = SubUser;
        this.SubProb = SubProb;
    }

    @Contract(pure = true)
    public Submission(String Sid, String SubText, String SubTime, String SubUser, String SubProb, int SubScore, String SubExam) {
        this.Sid = Sid;
        this.SubText = SubText;
        this.SubTime = SubTime;
        this.SubUser = SubUser;
        this.SubProb = SubProb;
        this.SubExam = SubExam;
        this.SubScore = SubScore;
    }


    public String getSubID() {
        return this.Sid;
    }

    String getSubProb() {
        return this.SubProb;
    }

    public String getSubText() {
        return this.SubText;
    }

    public String getSubTime() {
        return this.SubTime;
    }

    public String getSubUser() {
        return this.SubUser;
    }


    void setSubID(String Sid) {
        this.Sid = Sid;
    }

    boolean updateSubmission() {
        String cmd = "";
        try {
            Connection conn = getConnection();
            PreparedStatement sta;
            sta = prepareStatement("select * from submission where sid=?");
            sta.setString(1, this.Sid);
            ResultSet QueryResult = sta.executeQuery();
            if (isfaild(QueryResult)) {
                // 当前提交为新添加提交，执行insert
                sta = prepareStatement("insert into submission (stext, stime, suid, spid, sscore, seid, sid) values (" +
                        "?, now(), ?, ?, ?, ?, ?)");
            } else {
                //当前提交为内容更新，使用update
                sta = prepareStatement("update submission set " +
                        "stext=?, stime=now(), suid=?, spid=?, sscore=?, seid=? " +
                        "where sid=?");
            }
            sta.setString(1, this.SubText);
            sta.setString(2, this.SubUser);
            sta.setString(3, this.SubProb);
            sta.setInt(4, this.SubScore);
            sta.setString(5, this.SubExam);
            sta.setString(6, this.Sid);
            logger.debug("[problemservice]: updateSubmission: \n" + sta.toString());
            sta.executeUpdate();
            closeQuery(QueryResult, sta, conn);
            logger.debug("problemservice: Submission - \n\tupdate sid=" + this.Sid);
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage(),e);
            return false;
        }
    }


    private static boolean isfaild(@NotNull ResultSet rs) {
        boolean flag = true;
        try {
            while (rs.next()) {
                flag = false;
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(),e);
        }
        logger.error(flag ? "True" : "False");
        return flag;
    }
}
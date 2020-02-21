package com.onlinejudge.ProblemService;

import java.sql.*;

import static com.onlinejudge.DaemonService.DaemonServiceMain.*;
import static com.onlinejudge.util.DatabaseUtil.*;

public class Submission {
    private String Sid;
    private String SubText;
    private String SubTime;
    private String SubUser;
    private String SubProb;
    private String SubExam;
    private int SubScore;


    public Submission(String SubText, String SubUser, String SubProb, String SubExam, int SubScore) {
        this.Sid = "";
        this.SubText = SubText;
        this.SubUser = SubUser;
        this.SubProb = SubProb;
        this.SubExam = SubExam;
        this.SubScore = SubScore;
    }

    public Submission(String SubText, String SubUser, String SubProb, String SubExam) {
        // DeamonServiceRunnable重载
        // 提交到服务器的重载
        this.Sid = "";
        this.SubText = SubText;
        this.SubUser = SubUser;
        this.SubProb = SubProb;
        this.SubExam = SubExam;
    }

    public Submission(String Sid, String SubText, String SubTime, String SubUser, String SubProb) {
        // ObjectiveSubmission重载用
        this.Sid = Sid;
        this.SubText = SubText;
        this.SubTime = SubTime;
        this.SubUser = SubUser;
        this.SubProb = SubProb;
    }

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
            PreparedStatement sta = null;
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
            debugPrint("[ProblemService]: updateSubmission: \n" + sta.toString());
            sta.executeUpdate();
            closeQuery(QueryResult, sta, conn);
            debugPrint("ProblemService: Submission - \n\tupdate sid=" + this.Sid);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    private static boolean isfaild(ResultSet rs) {
        boolean flag = true;
        try {
            while (rs.next()) {
                flag = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(flag);
        return flag;
    }
}
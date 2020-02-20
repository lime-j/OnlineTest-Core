package com.onlinejudge.examservice;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import static com.onlinejudge.daemonservice.DaemonServiceMain.debugPrint;
import static com.onlinejudge.util.DatabaseUtil.*;

public class Exam {
    private String examID;
    private String examName;
    private String userID;
    private String startTime, endTime;
    private String examText;
    private List<String> problemList;
    private String examSubject;

    public Exam(String examID, String examName, String userID, String startTime, String endTime, String examText, String examSubject, List<String> pids) {
        this.examID = examID;
        this.examName = examName;
        this.userID = userID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.examText = examText;
        this.examSubject = examSubject;
        this.problemList = pids;
    }

    public Exam(String examID, String examName, String userID, String startTime, String endTime, String examText) {
        this.examID = examID;
        this.examName = examName;
        this.userID = userID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.examText = examText;
        this.problemList = new ArrayList<String>();
    }

    public Exam(String examID, String examName, String userID, String startTime, String endTime, String examText, String examSubject) {
        this.examID = examID;
        this.examName = examName;
        this.userID = userID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.examText = examText;
        this.problemList = new ArrayList<String>();
        this.examSubject = examSubject;

    }

    void updateExamPerm() {
        // 把该考试的所有者（数据库中为ownner列，本类中为userID列）加入允许考试列表
        try {
            getConnection();
//            PreparedStatement permExist = prepareStatement("select * from examperm where eid=? and sid=?");
//            ResultSet permExistResult;
//            permExist.setString(1, this.examID);
//            permExist.setString(2, this.userID);
//            permExistResult = permExist.executeQuery();
//            debugPrint("****wocnmd**** 找到没" + permExistResult.next());
//            if (!permExistResult.next()) {
            PreparedStatement updateStmt = prepareStatement("insert into examperm (eid, sid) values (?, ?)");
            updateStmt.setString(1, this.examID);
            updateStmt.setString(2, this.userID);
            debugPrint(this.toString() + " " + updateStmt.toString());
            updateStmt.executeUpdate();
            updateStmt.close();
//            }
//            permExistResult.close();
//            permExist.close();
            closeConnection();
        } catch (SQLIntegrityConstraintViolationException e) {
            debugPrint(this.toString() + " " + e.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getExamID() {
        return this.examID;
    }

    public void setExamID(String examID) {
        this.examID = examID;
    }

    public String getExamName() {
        return this.examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getUserID() {
        return this.userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getExamText() {
        return this.examText;
    }

    public void setExamText(String examText) {
        this.examText = examText;
    }

    public String getExamSubject() {
        return this.examSubject;
    }

    public void setExamSubject(String examSubject) {
        this.examSubject = examSubject;
    }

    public List<String> getProblemList() {
        return this.problemList;
    }

    public void setProblemList(List<String> problemList) {
        this.problemList = problemList;
    }
}

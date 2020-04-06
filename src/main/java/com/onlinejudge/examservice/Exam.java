package com.onlinejudge.examservice;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import static com.onlinejudge.util.DatabaseUtil.*;

@Getter
@Setter

public class Exam {
    private static final Logger log = LoggerFactory.getLogger(Exam.class);
    private String examID;
    private String examName;
    private String userID;
    private String startTime;
    private String endTime;
    private String examText;
    private List<String> problemList;
    private String examSubject;
    private int isRated;

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
        this.problemList = new ArrayList<>();
    }

    public Exam(String examID, String examName, String userID, String startTime, String endTime, String examText, String examSubject) {
        this.examID = examID;
        this.examName = examName;
        this.userID = userID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.examText = examText;
        this.problemList = new ArrayList<>();
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

            log.debug("{} {}", this, updateStmt);
            updateStmt.executeUpdate();
            updateStmt.close();
//            permExistResult.close();
//            permExist.close();
            closeConnection();
        } catch (SQLIntegrityConstraintViolationException e) {
            log.debug("{} {}", this, e.getMessage());
        } catch (SQLException e) {
            log.error("SQLEXception", e);
        }
    }
}

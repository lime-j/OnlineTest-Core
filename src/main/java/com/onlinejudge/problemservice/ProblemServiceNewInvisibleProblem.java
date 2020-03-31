package com.onlinejudge.problemservice;

import com.onlinejudge.util.StringEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import static com.onlinejudge.util.DatabaseUtil.*;

public class ProblemServiceNewInvisibleProblem implements StringEvent {
    private final InvisibleProblem currProblem;
    private final String oldPid;
    private final String examID;
    private static final Logger logger = LoggerFactory.getLogger(ProblemServiceNewInvisibleProblem.class);

    public ProblemServiceNewInvisibleProblem(@NotNull InvisibleProblem origin) {
        this.currProblem = origin;
        this.oldPid = origin.pid;
        this.examID = origin.examID;
    }

    @Override
    public void beforeGo() {
        // do nothing
    }

    @Override
    public void afterGo() {
        // do nothing
    }

    @Override
    public String go() {
        // 考试中编辑了某道题，则创建不可显见题目
        // requestType: editProblemFromExam

        // 查询旧题目的科目、知识点
        try {
            getConnection();
            PreparedStatement sta = prepareStatement("select * from problem where pid=?");
            sta.setString(1, this.currProblem.pid);
            logger.info("[problemservice]: Find the old problem: " + sta.toString());
            var queryResult = sta.executeQuery();
            if (queryResult.next()) {
                this.currProblem.probSubject = queryResult.getString("psubject");
                this.currProblem.probTag = queryResult.getString("ptag");
            }


            // 给新题目Pid并添加进数据库
//            if (this.currProblem.getProbData("pid").isEmpty()) {
            String pid = UUID.randomUUID().toString().replace('-', 'a').substring(0, 6);
            while (this.currProblem.addPid(pid)) {
                //noinspection DuplicateExpressions
                pid = UUID.randomUUID().toString().replace('-', 'a').substring(0, 6);
            }
            logger.info(String.format("Problem Pid: %s", pid));
//            }
            closeConnection();

            if (!this.currProblem.updateProb()) return "";
            if (!this.currProblem.updateVisible()) return "";


            // 将新题目对应到考试中
            logger.info("[problemservice]: this.currProblem.Pid=" + pid + ", this.currProblem.examID=" + this.examID +
                    ", this.oldPid=" + this.oldPid);
            getConnection();
            PreparedStatement staDelete = prepareStatement("delete from examprob where eid=? and pid=?");
            staDelete.setString(1, this.examID);
            staDelete.setString(2, this.oldPid);
            logger.info("[problemservice]: " + staDelete.toString());
            staDelete.executeUpdate();

            PreparedStatement staInsert = prepareStatement("insert into examprob (eid, pid) values (?, ?)");
            staInsert.setString(1, this.examID);
            staInsert.setString(2, pid);
            logger.debug("[problemservice]: New InvisibleProblem to exam SQL:\n\t" + sta.toString());
            staInsert.executeUpdate();
            queryResult.close();
            sta.close();
            staDelete.close();
            staInsert.close();
            closeConnection();
            return this.currProblem.pid;
        } catch (SQLException e) {
            logger.info(e.getMessage(), e);
            return "";
        }
    }
}

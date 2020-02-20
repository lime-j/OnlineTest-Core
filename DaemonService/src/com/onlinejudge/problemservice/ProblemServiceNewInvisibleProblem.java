package com.onlinejudge.problemservice;

import com.onlinejudge.util.StringEvent;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import static com.onlinejudge.daemonservice.DaemonServiceMain.debugPrint;
import static com.onlinejudge.util.DatabaseUtil.*;

public class ProblemServiceNewInvisibleProblem extends StringEvent {
    private InvisibleProblem currProblem;
    private String oldPid;
    private String examID;

    public ProblemServiceNewInvisibleProblem(InvisibleProblem origion) {
        this.currProblem = origion;
        this.oldPid = origion.Pid;
        this.examID = origion.examID;
    }

    @Override
    public String go() {
        // 考试中编辑了某道题，则创建不可显见题目
        // requestType: editProblemFromExam

        // 查询旧题目的科目、知识点
        try {
            getConnection();
            PreparedStatement sta = prepareStatement("select * from problem where pid=?");
            sta.setString(1, this.currProblem.Pid);
            debugPrint("[ProblemService]: Find the old problem: " + sta.toString());
            var queryResult = sta.executeQuery();
            if (queryResult.next()) {
                this.currProblem.ProbSubject = queryResult.getString("psubject");
                this.currProblem.ProbTag = queryResult.getString("ptag");
            }


            // 给新题目Pid并添加进数据库
//            if (this.currProblem.getProbData("pid").isEmpty()) {
            String pid = UUID.randomUUID().toString().replace('-', 'a').substring(0, 6);
            while (!this.currProblem.addPid(pid)) {
                pid = UUID.randomUUID().toString().replace('-', 'a').substring(0, 6);
            }
            System.out.println(String.format("Problem Pid: %s", pid));
//            }
            closeConnection();

            if (!this.currProblem.updateProb()) return "";
            if (!this.currProblem.updateVisible()) return "";


            // 将新题目对应到考试中
            debugPrint("[ProblemService]: this.currProblem.Pid=" + pid + ", this.currProblem.examID=" + this.examID +
                    ", this.oldPid=" + this.oldPid);
            getConnection();
            PreparedStatement staDelete = prepareStatement("delete from examprob where eid=? and pid=?");
            staDelete.setString(1, this.examID);
            staDelete.setString(2, this.oldPid);
            debugPrint("[ProblemService]: " + staDelete.toString());
            staDelete.executeUpdate();

            PreparedStatement staInsert = prepareStatement("insert into examprob (eid, pid) values (?, ?)");
            staInsert.setString(1, this.examID);
            staInsert.setString(2, pid);
            debugPrint("[ProblemService]: New InvisibleProblem to exam SQL:\n\t" + sta.toString());
            staInsert.executeUpdate();
            queryResult.close();
            sta.close();
            staDelete.close();
            staInsert.close();
            closeConnection();
            return this.currProblem.Pid;
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }
}

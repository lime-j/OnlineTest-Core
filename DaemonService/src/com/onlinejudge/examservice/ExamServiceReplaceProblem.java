package com.onlinejudge.examservice;

import com.onlinejudge.problemservice.Problem;
import com.onlinejudge.util.BooleanEvent;
import com.onlinejudge.util.ListEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.onlinejudge.daemonservice.DaemonServiceMain.debugPrint;
import static com.onlinejudge.util.DatabaseUtil.*;

public class ExamServiceReplaceProblem extends ListEvent {
    private String examID, newID, oldID;

    public ExamServiceReplaceProblem(String examID, String newID, String oldID) {
        this.examID = examID;
        this.newID = newID;
        this.oldID = oldID;
    }

    @Override
    public List<Problem> go() {
        // 考试中新题目替换旧题目
        // requestType: replaceProblemFromExam
        List<Problem> newProblem = new ArrayList<>();
        try {
            Connection conn = getConnection();
            PreparedStatement staQuery = prepareStatement("select * from problem where pid=?");
            PreparedStatement staUpdate = prepareStatement("delete from examprob where eid=? and pid=?");

            // 查询试题是否存在
            staQuery.setString(1, this.newID);
            debugPrint("[ExamService]: ReplaceProblem query old: " + staQuery.toString());
            var queryResult = staQuery.executeQuery();
            if (!queryResult.next()) {
                closeConnection();
                queryResult.close();
                staQuery.close();
                staUpdate.close();
                return newProblem;
            }
            Problem curr = new Problem(queryResult.getInt("ptype"), queryResult.getString("pid"),
                    queryResult.getString("ptitle"), queryResult.getString("ptext"), "", queryResult.getInt("pmaxsize"),
                    queryResult.getInt("pmaxtime"), queryResult.getInt("pscore"), queryResult.getString("psubject"),
                    queryResult.getString("ptag"));


            staUpdate.setString(1, this.examID);
            staUpdate.setString(2, this.oldID);
            debugPrint("[ExamService]: ReplaceProblem update: " + staUpdate.toString());
            staUpdate.executeUpdate();
            staUpdate.close();
            staUpdate = prepareStatement("insert into examprob (eid, pid) values (?, ?)");
            staUpdate.setString(1, this.examID);
            staUpdate.setString(2, this.newID);

            newProblem.add(curr);

            queryResult.close();
            staQuery.close();
            staUpdate.close();
            closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return newProblem;
        }
        return newProblem;
    }
}

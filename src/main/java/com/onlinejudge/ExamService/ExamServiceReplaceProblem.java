package com.onlinejudge.examservice;

import com.onlinejudge.problemservice.Problem;
import com.onlinejudge.util.ListEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.onlinejudge.util.DatabaseUtil.*;

public class ExamServiceReplaceProblem extends ListEvent {
    private String examID, newID, oldID;
    private static Logger logger = LoggerFactory.getLogger(ExamServiceReplaceProblem.class);

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
        ResultSet queryResult = null;
        PreparedStatement staQuery = null;
        PreparedStatement staUpdate = null;
        try {
            Connection conn = getConnection();
            staQuery = prepareStatement("select * from problem where pid=?");
            staUpdate = prepareStatement("delete from examprob where eid=? and pid=?");

            // 查询试题是否存在
            staQuery.setString(1, this.newID);
            logger.info("ReplaceProblem query old: " + staQuery.toString());
            queryResult = staQuery.executeQuery();
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
            logger.info("ReplaceProblem update: " + staUpdate.toString());
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
            logger.error(e.getMessage(), e);
            try {
                Objects.requireNonNull(staQuery).close();
                Objects.requireNonNull(staUpdate).close();
                Objects.requireNonNull(queryResult).close();
                closeConnection();
            } catch (SQLException ex) {
                logger.error(ex.getMessage(), ex);
            }
            return newProblem;
        }
        return newProblem;
    }
}
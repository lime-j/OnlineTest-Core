package com.onlinejudge.ProblemService;

import com.onlinejudge.util.BooleanEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.onlinejudge.util.DatabaseUtil.*;

public class ProblemServiceDeleteProblem extends BooleanEvent {
    private String problemID;

    public ProblemServiceDeleteProblem(String problemID) {
        this.problemID = problemID;
    }

    @Override
    public boolean go() {
        // 从题库中删除问题
        // requestType: deleteProblemFromDatabase
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = prepareStatement("delete from problem where pid = ?");
            stmt.setString(1, this.problemID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                closeUpdate(stmt, conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public String getProblemID() {
        return this.problemID;
    }

    public void setProblemID(String problemID) {
        this.problemID = problemID;
    }
}

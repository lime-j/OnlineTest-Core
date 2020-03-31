package com.onlinejudge.problemservice;

import com.onlinejudge.util.BooleanEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.onlinejudge.util.DatabaseUtil.closeUpdate;
import static com.onlinejudge.util.DatabaseUtil.getConnection;
import static com.onlinejudge.util.DatabaseUtil.prepareStatement;

@Log4j2
@Getter
@Setter
public class ProblemServiceDeleteProblem implements BooleanEvent {
    private String problemID;

    public ProblemServiceDeleteProblem(String problemID) {
        this.problemID = problemID;
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
            log.error(e.getMessage(), e);
            return false;
        } finally {
            try {
                closeUpdate(stmt, conn);
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }
        return true;
    }

}

package com.onlinejudge.problemservice;

import com.onlinejudge.util.BooleanEvent;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.onlinejudge.util.DatabaseUtil.*;


@Getter
@Setter
public class ProblemServiceDeleteProblem implements BooleanEvent {
    private String problemID;
    private static final Logger log = LoggerFactory.getLogger(ProblemServiceDeleteProblem.class);
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

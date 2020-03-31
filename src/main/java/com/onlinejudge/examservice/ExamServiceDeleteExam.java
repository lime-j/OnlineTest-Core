package com.onlinejudge.examservice;

import com.onlinejudge.util.BooleanEvent;
import com.onlinejudge.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.onlinejudge.util.DatabaseUtil.closeUpdate;
import static com.onlinejudge.util.DatabaseUtil.prepareStatement;

public class ExamServiceDeleteExam implements BooleanEvent {
    public final String examID;
    private static final Logger logger = LoggerFactory.getLogger(ExamServiceDeleteExam.class);

    public ExamServiceDeleteExam(String examID) {
        this.examID = examID;
    }

    @Override
    public void beforeGo() {
        // do nothing
    }

    @Override
    public void afterGo() {
        // do nothing
    }

    public boolean go() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseUtil.getConnection();
            //stmt.execute("use onlinejudge");
            String qry = String.format("delete from exam where eid = '%s';", this.examID);
            stmt = prepareStatement(qry);
            logger.debug(qry);
            stmt.execute();
            closeUpdate(stmt, conn);
            return true;
        } catch (SQLException e) {
            try {
                closeUpdate(stmt, conn);
            } catch (SQLException ex) {
                logger.error(ex.getMessage(),ex);
            }
            logger.error(e.getMessage(),e);
            return false;
        }
    }


}

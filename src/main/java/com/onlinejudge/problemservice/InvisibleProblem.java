package com.onlinejudge.problemservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.onlinejudge.util.DatabaseUtil.*;

public class InvisibleProblem extends Problem {

    private boolean visible = false;
    final String examID;
    private static final Logger logger = LoggerFactory.getLogger(InvisibleProblem.class);

    public InvisibleProblem(int ptype, String pid, String ptitle, String ptext, String pans, int pmaxsize, int pmaxtime, int pscore, String examID, String pSubject, String pTag) {
        super(ptype, pid, ptitle, ptext, pans, pmaxsize, pmaxtime, pscore, pSubject, pTag);
        this.examID = examID;
        logger.info("ProblemInvisible: examID=" + this.examID);
    }

    public boolean updateVisible() {
        try {
            Connection conn = getConnection();
            String cmd = String.format("update problem set visible=0 where pid='%s'", this.Pid);
            logger.debug("[problemservice]: updateVisible: " + cmd);
            PreparedStatement sta = prepareStatement(cmd);
            sta.executeUpdate();
            closeUpdate(sta, conn);
        } catch (SQLException e) {
            logger.error(e.getMessage(),e);
            return false;
        }
        return true;
    }
}

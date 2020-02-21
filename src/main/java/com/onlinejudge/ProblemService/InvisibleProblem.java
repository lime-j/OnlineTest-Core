package com.onlinejudge.ProblemService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.onlinejudge.DaemonService.DaemonServiceMain.debugPrint;
import static com.onlinejudge.util.DatabaseUtil.*;

public class InvisibleProblem extends Problem {

    private boolean visible = false;
    String examID;

    public InvisibleProblem(int ptype, String pid, String ptitle, String ptext, String pans, int pmaxsize, int pmaxtime, int pscore, String examID, String pSubject, String pTag) {
        super(ptype, pid, ptitle, ptext, pans, pmaxsize, pmaxtime, pscore, pSubject, pTag);
        this.examID = examID;
        debugPrint("[ProblemService]: ProblemInvisible: examID=" + this.examID);
    }

    public boolean updateVisible() {
        try {
            Connection conn = getConnection();
            String cmd = String.format("update problem set visible=0 where pid='%s'", this.Pid);
            debugPrint("[ProblemService]: updateVisible: " + cmd);
            PreparedStatement sta = prepareStatement(cmd);
            sta.executeUpdate();
            closeUpdate(sta, conn);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

package com.onlinejudge.problemservice;

import com.onlinejudge.util.BooleanEvent;
import com.onlinejudge.util.DatabaseUtil;
import com.onlinejudge.util.InternalException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ProblemServiceSetSubject extends BooleanEvent {
    private static final Logger logger = LoggerFactory.getLogger(ProblemServiceSetSubject.class);
    public ProblemServiceSetSubject(@NotNull List<String> subject, String userID){
        this.subject = subject;
        this.userID = userID;
    }
    List<String> subject;
    public final String userID;

    public boolean go() throws InternalException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try{
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("delete from teasubject where teacherid = ?");
            stmt.setString(1, userID);
            stmt.execute();
            stmt.close();

            stmt = conn.prepareStatement("insert into teasubject values(?, ?)");
            for (var subjectName :subject){
                stmt.setString(1, subjectName);
                stmt.setString(2,userID);
                stmt.execute();
            }
        }catch (SQLException e){
            logger.error(e.getMessage(),e);
        }finally{
            try {
                DatabaseUtil.closeUpdate(stmt, conn);
            } catch (SQLException e) {
                logger.error(e.getMessage(),e);
            }
        }
        return true;
    }
}

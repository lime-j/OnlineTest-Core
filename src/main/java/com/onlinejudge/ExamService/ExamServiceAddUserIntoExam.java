package com.onlinejudge.ExamService;

import com.alibaba.fastjson.JSONObject;
import com.onlinejudge.util.BooleanEvent;
import com.onlinejudge.util.DatabaseUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static com.onlinejudge.DaemonService.DaemonServiceMain.debugPrint;

public class ExamServiceAddUserIntoExam extends BooleanEvent {
    private List<String> currentUserList;
    private String examID;

    public ExamServiceAddUserIntoExam(List<String> Origin, String examID) {
        this.currentUserList = Origin;
        this.examID = examID;
    }

    @Override
    public boolean go() {
        try {
            var conn = DatabaseUtil.getConnection();
            ResultSet queryResult = null;
            PreparedStatement staQuery = conn.prepareStatement("select * from examperm where eid= ? and sid= ? ;");
            for (String s : this.currentUserList) {
                JSONObject currJSONG = JSONObject.parseObject(s);
                String cmd;

                staQuery.setString(1, this.examID);
                staQuery.setString(2, currJSONG.getString("uid"));

                // debug only
                debugPrint(staQuery.toString());
                //

                queryResult = staQuery.executeQuery();
                PreparedStatement staInsert = conn.prepareStatement("insert into examperm (eid,sid) values (?, ?)");
                if (!queryResult.next()) {
                    staInsert.setString(1, this.examID);
                    staInsert.setString(2, currJSONG.getString("uid"));
                    debugPrint(staInsert.toString());
                    staInsert.executeUpdate();
                }
                queryResult.close();
                staInsert.close();
            }
            DatabaseUtil.closeQuery(queryResult, staQuery, conn);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

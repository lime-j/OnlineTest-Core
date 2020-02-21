package com.onlinejudge.ManagementService;

import com.alibaba.fastjson.JSONObject;
import com.onlinejudge.util.BooleanEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.onlinejudge.DaemonService.DaemonServiceMain.debugPrint;
import static com.onlinejudge.util.DatabaseUtil.*;

public class ManagerServiceTeacherSubject extends BooleanEvent {
    private List<String> currTeacherSubject;

    public ManagerServiceTeacherSubject(List<String> origionData) {
        this.currTeacherSubject = origionData;
    }

    @Override
    public boolean go() {
        // 将教师执教科目插入到数据库
        // requestType: addTeacherSubject
        try {
            Connection conn = getConnection();
            PreparedStatement staInsertTea = prepareStatement("insert into teasubject (subject, teacherid)" +
                    "values(?, ?)");
            PreparedStatement staNewSubject = prepareStatement("insert into subject (subject)" +
                    "values(?)");
            PreparedStatement staQuerySubject = prepareStatement("select * from subject where subject=?");
            PreparedStatement staQueryTea = prepareStatement("select * from teasubject where subject=? and teacherid=?");
            for (String s : this.currTeacherSubject) {
                JSONObject currJSON = JSONObject.parseObject(s);
                String currSubject = currJSON.getString("Subject");
                String currTeacherID = currJSON.getString("teacherID");
                staQuerySubject.setString(1, currSubject);
                debugPrint("[ManagerService]: Query Subject: " + staQuerySubject.toString());
                var querySet = staQuerySubject.executeQuery();
                if (!querySet.next()) {
                    staNewSubject.setString(1, currSubject);
                    debugPrint("[ManagerService]: Insert new Subject: " + staNewSubject.toString());
                    staNewSubject.executeUpdate();
                }
                querySet.close();

                // 查询当前教师执教科目是否存在
                staQueryTea.setString(1, currSubject);
                staQueryTea.setString(2, currTeacherID);
                debugPrint("[ManagerService]: Query Teacher and Subject: " + staQueryTea.toString());
                querySet = staQueryTea.executeQuery();
                if (!querySet.next()) {
                    staQueryTea.close();
                    staQueryTea = prepareStatement("select * from userinfo where uid=?");
                    staQueryTea.setString(1, currTeacherID);
                    debugPrint("[ManagerService]: " + this.toString() + ": " + staQueryTea.toString());
                    querySet = staQueryTea.executeQuery();
                    if (querySet.next()) {
                        staInsertTea.setString(1, currSubject);
                        staInsertTea.setString(2, currTeacherID);
                        debugPrint("[ManagerService]: Insert into TeaSubject: " + staInsertTea.toString());
                        staInsertTea.executeUpdate();
                    }
                }
            }
            staInsertTea.close();
            staNewSubject.close();
            staQuerySubject.close();
            staQueryTea.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

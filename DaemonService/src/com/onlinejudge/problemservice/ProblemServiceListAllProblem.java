package com.onlinejudge.problemservice;

import com.onlinejudge.util.ListEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.onlinejudge.daemonservice.DaemonServiceMain.debugPrint;
import static com.onlinejudge.util.DatabaseUtil.*;

public class ProblemServiceListAllProblem extends ListEvent {
    private String subject;
    private List<String> tagList;

    public ProblemServiceListAllProblem(String subject, List<String> tagList) {
        // 从题库中拉取题目列表（制定subject与tagList）
        // requestType; listProblemFromDatabase
        this.subject = subject;
        this.tagList = tagList;
    }


    @Override
    public List<Problem> go() {
        List<Problem> resultList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet result = null;
        try {
            conn = getConnection();
            stmt = prepareStatement("select * from problem where psubject = ? and ptag = ? and visible=1");
            stmt.setString(1, this.subject);
            for (String tag : this.tagList) {
                stmt.setString(2, tag);
                debugPrint(this.toString() + ", " + stmt.toString());
                result = stmt.executeQuery();
                while (result.next()) {
                    resultList.add(new Problem(
                            result.getInt("ptype"), result.getString("pid"), result.getString("ptitle")
                            , result.getString("ptext"), result.getString("pans"), result.getInt("pmaxsize"),
                            result.getInt("pmaxsize"), result.getInt("pscore")
                            , result.getString("psubject"), result.getString("ptag")
                    ));
                }
                result.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                closeUpdate(stmt, conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return resultList;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<String> getTagList() {
        return this.tagList;
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }
}

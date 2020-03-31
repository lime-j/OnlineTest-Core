package com.onlinejudge.problemservice;

import com.onlinejudge.util.ListEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static com.onlinejudge.util.DatabaseUtil.*;


public class ProblemServiceListAllProblem extends ListEvent {
    private String subject;
    private List<String> tagList;
    private static final Logger logger = LoggerFactory.getLogger(ProblemServiceListAllProblem.class);

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
        ResultSet result;
        try {
            conn = getConnection();
            stmt = prepareStatement("select * from problem where psubject = ? and ptag = ? and visible=1");
            stmt.setString(1, this.subject);
            for (String tag : this.tagList) {
                stmt.setString(2, tag);
                logger.info(MessageFormat.format("{0}, {1}", this.toString(), stmt.toString()));
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
            logger.error(e.getMessage(),e);
        } finally {
            try {
                closeUpdate(stmt, conn);
            } catch (SQLException e) {
                logger.error(e.getMessage(),e);
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

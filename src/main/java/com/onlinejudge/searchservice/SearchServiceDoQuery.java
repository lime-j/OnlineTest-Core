package com.onlinejudge.searchservice;

import com.onlinejudge.util.DatabaseUtil;
import com.onlinejudge.util.ListEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.onlinejudge.util.DatabaseUtil.prepareStatement;


public class SearchServiceDoQuery implements ListEvent<SearchServiceResult> {
    //searchservice 提供考试和题面查询功能,
    // 调用参数 :
    // userID 表示用户的ID (不是Token)
    // queryType 表示查询类型, 1 表示是问题查询, 2 表示是考试查询
    // keyword 是匹配关键字
    // 返回 JSON String, 以一个list的形式给出,
    // 每个 List 内包括多个JSON String, 内容如 SearchServiceResult 所示.

    private String userID;
    private QueryTypes queryType;
    private String keyword;
    private static final Logger logger = LoggerFactory.getLogger(SearchServiceDoQuery.class);

    public SearchServiceDoQuery(String userID, int queryType, String keyword) {
        setUserID(userID);
        setQueryType(queryType);
        setKeyword(keyword);
    }

    @Override
    public void beforeGo() {
        // do nothing
    }

    @Override
    public void afterGo() {
        // do nothing
    }

    private List<SearchServiceResult> handleProblem(ResultSet queryResult) throws SQLException {
        List<SearchServiceResult> resultList = new ArrayList<>();
        int cnt = 0;
        while (queryResult.next()) {
            ++cnt;
            String ptext = queryResult.getString("ptext");
            String pTitle = queryResult.getString("ptitle");
            String pID = queryResult.getString("pid");
            var result = new SearchServiceResult(ptext, pID, pTitle);
            logger.debug("find ptext = {},ptitle = {},pid = {}", ptext, pTitle, pID);
            resultList.add(result);
        }
        logger.info("searchservice, find {} result(s).", cnt);
        return resultList;
    }

    private List<SearchServiceResult> handleContest(ResultSet queryResult) throws SQLException {
        List<SearchServiceResult> resultList = new ArrayList<>();
        int cnt = 0;
        while (queryResult.next()) {
            ++cnt;
            String eTitle = queryResult.getString("etitle");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            var eStartTime = formatter.format(new java.util.Date(queryResult.getTimestamp("estart").getTime()));
            var eEndTime = formatter.format(new java.util.Date(queryResult.getTimestamp("eend").getTime()));
            var description = eStartTime + "|" + eEndTime;
            var eID = queryResult.getString("eid");
            var eSubject = queryResult.getString("esubject");
            var result = new SearchServiceResult(description, eID, eTitle, eSubject);
            logger.debug("searchservice, find description = " + description + ",eID = " + eID + ",eTitle = " + eTitle);
            resultList.add(result);
        }
        logger.info("searchservice, find " + cnt + " result(s).");
        return resultList;
    }

    public List<SearchServiceResult> go() {
        // return a JSON string contains list of SearchServiceResult
        Connection conn;
        PreparedStatement stmt;
        ResultSet queryResult;
        int cnt = 0;
        try {
            //  Class.forName(JDBC_DRIVER);
            conn = DatabaseUtil.getConnection();
            logger.info("searchservice, conn settled.");
            List<SearchServiceResult> resultList = null;

            stmt = prepareStatement("select utype from userinfo where uid = ?");
            stmt.setString(1, this.userID);
            queryResult = stmt.executeQuery();
            int userCount = 0;
            int userType = -1;
            while (queryResult.next()) {
                userCount++;
                userType = queryResult.getInt("utype");
            }
            queryResult.close();
            stmt.close();
            // userCount must be 1 or 0, or something wrong happened
            assert (userCount == 1 || userCount == 0);
            // administrator can't do search, if userType == 1, something wrong happened
            assert (userType != 1);


            if (this.queryType == QueryTypes.PROBLEM) {
                logger.info("searchservice, queryType == Problem");
                String qry;
                if (userType == 3) {
                    qry = "select p.ptext, p.ptitle ptitle, p.pid from examperm ep, exam e, problem p where (e.eid = ep.eid) and (ep.sid = ?) and (ptitle like ? or ename like ? or ptag like ? or psubject like ?)";
                    stmt = prepareStatement(qry);
                    String tmp = "%%" + this.keyword + "%%";
                    stmt.setString(1, this.userID);
                    stmt.setString(2, tmp);
                    stmt.setString(3, tmp);
                    stmt.setString(4, tmp);
                    stmt.setString(5, tmp);
                } else if (userType == 2) {
                    qry = "select p.ptitle, p.pid, p.ptext from teasubject ts, problem p where(ts.teacherid = ?) and (ts.subject = p.psubject) and (ptitle like ? or psubject like ? or ptag like ?)";
                    stmt = prepareStatement(qry);
                    String tmp = "%%" + this.keyword + "%%";
                    stmt.setString(1, this.userID);
                    stmt.setString(2, tmp);
                    stmt.setString(3, tmp);
                    stmt.setString(4, tmp);
                }

                //String qry = String.format("select e.ename etitle, p.ptitle ptitle, p.pid from examperm ep, exam e, problem p where (e.eid = ep.eid) and (ep.sid = '%s') and  (ptitle like '%%%s%%' or ename like '%%%s%%' or ptag like %%%s%% or psubject like %%%s%%);", this.userID, this.keyword, this.keyword);
                //stmt = prepareStatement(qry);
                queryResult = stmt.executeQuery();
                resultList = handleProblem(queryResult);
            } else if (this.queryType == QueryTypes.CONTEST) {
                String qry = "select e.eid, e.ename etitle, e.estart, e.eend, e.esubject from examperm ep, exam e where e.eid = ep.eid and ep.sid = ? and  ename like  ?";
                stmt = prepareStatement(qry);
                stmt.setString(1, this.userID);
                String tmp = "%%" + this.keyword + "%%";
                stmt.setString(2, tmp);
                queryResult = stmt.executeQuery();
                resultList = handleContest(queryResult);
            }
            DatabaseUtil.closeQuery(queryResult, stmt, conn);
            return resultList;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e, e.getSQLState());
            return new ArrayList<>();
        }
    }

    public String getUserID() {
        return this.userID;
    }

    private void setUserID(String userID) {
        this.userID = userID;
    }

    public QueryTypes getQueryType() {
        return this.queryType;
    }

    private void setQueryType(int queryType) {
        if (queryType == 1) this.queryType = QueryTypes.PROBLEM;
        else this.queryType = QueryTypes.CONTEST;
    }

    public String getKeyword() {
        return this.keyword;
    }

    private void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}

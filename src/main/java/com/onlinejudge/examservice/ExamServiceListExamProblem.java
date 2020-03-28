package com.onlinejudge.examservice;

import com.onlinejudge.problemservice.Problem;
import com.onlinejudge.util.ListEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.onlinejudge.util.DatabaseUtil.*;

public class ExamServiceListExamProblem extends ListEvent<Problem> {
    private final String examID;
    private static final Logger logger = LoggerFactory.getLogger(ExamServiceListExamProblem.class);

    public ExamServiceListExamProblem(String examID) {
        this.examID = examID;
    }

    public List<Problem> go() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet queryResult = null;
        try {
            //   Class.forName(JDBC_DRIVER);
            conn = getConnection();
            String qry = String.format("select ep.pid, ptitle, ptext, ptype, pmaxsize, pmaxtime, pscore, psubject, ptag, visible, pcount " +
                    "from examprob ep, problem p where ep.pid = p.pid and ep.eid = '%s'\n", this.examID);
            stmt = prepareStatement(qry);
            logger.info("conn and stmt settled.");
            //stmt.executeQuery("use onlinejudge");
            logger.debug(MessageFormat.format("ExamServiceListExamProblem, qry = {0}", qry));
            queryResult = stmt.executeQuery();
            int cnt = 0;
            List<Problem> resultList = new ArrayList<>();
            HashMap<Integer, Problem> vis = new HashMap<>();
            HashMap<Integer, List<Integer>> lsts = new HashMap<>();
            HashMap<String, Integer> countMap = new HashMap<>();
            List<Integer> tempChoice = new ArrayList<>();
            List<Integer> tempTorF = new ArrayList<>();
            List<Integer> tempBlank = new ArrayList<>();
            List<Integer> tempSubjective = new ArrayList<>();
            List<Integer> tempProgramBlank = new ArrayList<>();
            List<Integer> tempProgram = new ArrayList<>();
            lsts.put(1, tempChoice);
            lsts.put(3, tempBlank);
            lsts.put(2, tempTorF);
            lsts.put(5, tempSubjective);
            lsts.put(10, tempProgram);
            lsts.put(11, tempProgramBlank);

            while (queryResult.next()) {
                ++cnt;
                String pID = queryResult.getString("pid");
                var ptitle = queryResult.getString("ptitle");
                var ptext = queryResult.getString("ptext");
                var pans = "";
                int ptype = queryResult.getInt("ptype");
                int psz = queryResult.getInt("pmaxsize");
                int ptm = queryResult.getInt("pmaxtime");
                int psc = queryResult.getInt("pscore");
                String ptg = queryResult.getString("ptag");
                String psg = queryResult.getString("psubject");
                var result = new Problem(ptype, pID, ptitle, ptext, pans, psz, ptm, psc, psg, ptg);
                logger.info("find {} results",cnt);
                vis.put(cnt, result);
                lsts.get(queryResult.getInt("ptype")).add(cnt);
            }
            stmt.close();
            closeQuery(queryResult, stmt, conn);
            logger.info(MessageFormat.format("[DBG]: find{0}result(s)", cnt));
            if (tempBlank.size() >= 1) Collections.shuffle(tempBlank);
            if (tempChoice.size() >= 1) Collections.shuffle(tempChoice);
            if (tempProgram.size() >= 1) Collections.shuffle(tempProgram);
            if (tempProgramBlank.size() >= 1) Collections.shuffle(tempProgramBlank);
            if (tempSubjective.size() >= 1) Collections.shuffle(tempSubjective);
            if (tempTorF.size() >= 1) Collections.shuffle(tempTorF);
            List<Integer> tmp = new ArrayList<>();
            tmp.addAll(tempChoice);
            tmp.addAll(tempTorF);
            tmp.addAll(tempBlank);
            tmp.addAll(tempProgram);
            tmp.addAll(tempSubjective);
            tmp.addAll(tempProgramBlank);
            for (var it : tmp) {
                logger.debug(it.toString());
                resultList.add(vis.get(it));
            }
            return resultList;
        } catch (SQLException sqlException) {
            logger.error(sqlException.getMessage(),sqlException);
            try {
                closeQuery(queryResult, stmt, conn);
            } catch (SQLException e) {
                logger.error(e.getMessage(),e);
            }
            return new ArrayList<>();
        }
    }
}

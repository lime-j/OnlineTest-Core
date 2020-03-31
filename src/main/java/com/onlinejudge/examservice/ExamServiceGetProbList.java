package com.onlinejudge.examservice;

import com.onlinejudge.problemservice.Problem;
import com.onlinejudge.util.Provider;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.onlinejudge.util.DatabaseUtil.*;

@Log4j2
public class ExamServiceGetProbList implements Provider {
    @NotNull
    public static List<Problem> getItem(String examID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet queryResult = null;
        List<Problem> resultList = new ArrayList<>();
        try {
            conn = getConnection();
            String qry = String.format("select ep.pid, ptitle, ptext, ptype, pmaxsize, pmaxtime, pscore, psubject, ptag, visible, pcount " +
                    "from examprob ep, problem p where ep.pid = p.pid and ep.eid = '%s'\n", examID);
            stmt = prepareStatement(qry);
            log.debug("qry = {}", qry);
            queryResult = stmt.executeQuery();
            int cnt = 0;
            HashMap<Integer, Problem> vis = new HashMap<>();
            HashMap<Integer, List<Integer>> lsts = new HashMap<>();
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
                String psg = queryResult.getString("psubject");
                var result = new Problem(ptype, pID, ptitle, ptext, pans, psz, ptm, psc, psg);
                log.info("find {} results", cnt);
                vis.put(cnt, result);
                lsts.get(queryResult.getInt("ptype")).add(cnt);
            }
            log.info("[DBG]: find{}result(s)", cnt);
            List<Integer> tmp = new ArrayList<>();
            tmp.addAll(tempChoice);
            tmp.addAll(tempTorF);
            tmp.addAll(tempBlank);
            tmp.addAll(tempProgram);
            tmp.addAll(tempSubjective);
            tmp.addAll(tempProgramBlank);
            for (var it : tmp) {
                log.debug("{}", it);
                resultList.add(vis.get(it));
            }
        } catch (SQLException sqlException) {
            log.error(sqlException.getMessage(), sqlException);
        } finally {
            try {
                closeQuery(queryResult, stmt, conn);
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }
        return resultList;
    }
}

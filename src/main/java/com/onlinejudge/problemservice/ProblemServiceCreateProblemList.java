package com.onlinejudge.problemservice;

import com.onlinejudge.util.ListEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.onlinejudge.util.DatabaseUtil.*;
import static java.util.Collections.sort;

public class ProblemServiceCreateProblemList extends ListEvent {
    private String subject;
    private List<String> tagList;
    private int choice, torf, blank, subjective, progblank, prog;
    private static final Logger logger = LoggerFactory.getLogger(ProblemServiceCreateProblemList.class);

    public ProblemServiceCreateProblemList(String subject, List<String> tagList, int choice, int torf, int blank, int subjective, int progblank, int prog) {
        // 创建试题列表
        // requestType: createProblemList
        this.subject = subject;
        this.tagList = tagList;
        this.choice = choice;
        this.torf = torf;
        this.blank = blank;
        this.subjective = subjective;
        this.progblank = progblank;
        this.prog = prog;
    }

    public int getChoice() {
        return this.choice;
    }

    public void setChoice(int choice) {
        this.choice = choice;
    }

    public int getTorf() {
        return this.torf;
    }

    public void setTorf(int torf) {
        this.torf = torf;
    }

    public int getBlank() {
        return this.blank;
    }

    public void setBlank(int blank) {
        this.blank = blank;
    }

    public int getSubjective() {
        return this.subjective;
    }

    public void setSubjective(int subjective) {
        this.subjective = subjective;
    }

    public int getProg() {
        return this.prog;
    }

    public void setProg(int prog) {
        this.prog = prog;
    }

    public int getProgblank() {
        return this.progblank;
    }

    public void setProgblank(int progblank) {
        this.progblank = progblank;
    }

    @NotNull
    private List<Integer> gen(@NotNull List<Pair> lst, int lim) {
        List<Integer> ret = new ArrayList<>();
        int cnt = 0;
        for (var it : lst) {
            if (++cnt > lim) break;
            ret.add(it.first);
        }
        return ret;
    }

    @Override
    public List<Problem> go() {
        List<Problem> resultList = new ArrayList<>();

        List<Pair> tempChoice = new ArrayList<>();
        List<Pair> tempTorF = new ArrayList<>();
        List<Pair> tempBlank = new ArrayList<>();
        List<Pair> tempSubjective = new ArrayList<>();
        List<Pair> tempProgramBlank = new ArrayList<>();
        List<Pair> tempProgram = new ArrayList<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        try {
            conn = getConnection();
            stmt = prepareStatement("select * from problem where ptag = ?");
            int cnt = 0;
            HashMap<Integer, Problem> vis = new HashMap<>();
            HashMap<Integer, List<Pair>> lsts = new HashMap<>();
            HashMap<String, Integer> countMap = new HashMap<>();
            lsts.put(1, tempChoice);
            lsts.put(3, tempBlank);
            lsts.put(2, tempTorF);
            lsts.put(5, tempSubjective);
            lsts.put(10, tempProgram);
            lsts.put(11, tempProgramBlank);
            for (String tag : this.tagList) {
                stmt.setString(1, tag);
                resultSet = stmt.executeQuery();
                while (resultSet.next()) {
                    if (resultSet.getInt("visible") == 0) continue;
                    logger.info(String.format("pid=%s, ptype=%d, ptitle=%s, ptext=%s, pans=%s, pmaxsize=%d, pmaxtime=%d, psocre=%d, psubject=%s, ptag=%s",
                            resultSet.getString("pid"),
                            resultSet.getInt("ptype"),
                            resultSet.getString("ptitle"),
                            resultSet.getString("ptext"),
                            "",
                            resultSet.getInt("pmaxsize"),
                            resultSet.getInt("pmaxtime"),
                            resultSet.getInt("pscore"),
                            resultSet.getString("psubject"),
                            resultSet.getString("ptag")));
                    vis.put(++cnt, new Problem(
                            resultSet.getInt("ptype"),
                            resultSet.getString("pid"),
                            resultSet.getString("ptitle"),
                            resultSet.getString("ptext"),
                            "",
                            resultSet.getInt("pmaxsize"),
                            resultSet.getInt("pmaxtime"),
                            resultSet.getInt("pscore"),
                            resultSet.getString("psubject"),
                            resultSet.getString("ptag")
                    ));
                    int pcount = resultSet.getInt("pcount");
                    lsts.get(resultSet.getInt("ptype")).add(new Pair(cnt, pcount));
                    countMap.put(resultSet.getString("pid"), resultSet.getInt("pcount"));
                }
                resultSet.close();
            }

            sort(tempBlank);
            sort(tempChoice);
            sort(tempProgram);
            sort(tempProgramBlank);
            sort(tempSubjective);
            sort(tempTorF);
            int cur = 0;
            List<Integer> tmp = new ArrayList<>();
            tmp.addAll(gen(tempChoice, this.choice));
            tmp.addAll(gen(tempTorF, this.torf));
            tmp.addAll(gen(tempBlank, this.blank));
            tmp.addAll(gen(tempProgram, this.prog));
            tmp.addAll(gen(tempSubjective, this.subjective));
            tmp.addAll(gen(tempProgramBlank, this.progblank));
            for (var it : tmp) resultList.add(vis.get(it));
            stmt.close();
            stmt = prepareStatement("update problem set pcount = ? where pid = ?");
            for (var it : resultList) {
                stmt.setInt(1, countMap.get(it.Pid) + 1);
                stmt.setString(2, it.Pid);
                stmt.executeUpdate();
            }
            closeUpdate(stmt, conn);
            return resultList;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                closeQuery(resultSet, stmt, conn);
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            }

        }

        return resultList;
    }

    public String getSubject() {
        return this.subject;
    }

    public List<String> getTagList() {
        return this.tagList;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public static class Pair implements Comparable {
        final int first;
        final int second;

        @Contract(pure = true)
        Pair(int first, int second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public int compareTo(@NotNull Object o) {
            if (this.second == ((Pair) o).second) return 0;
            return this.second > ((Pair) o).second ? 1 : -1;
        }
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }
}

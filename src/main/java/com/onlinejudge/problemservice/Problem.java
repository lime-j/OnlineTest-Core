package com.onlinejudge.problemservice;

import com.onlinejudge.util.DatabaseUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

public class Problem {
    private static final String HOME_PATH = "/tmp";
    private static final Logger logger = LoggerFactory.getLogger(Problem.class);

    /*    final private String homePath=System.getProperties().getProperty("user.home"); */
    public final int probType; //试题类型，1==选择题，2==填空题，3==程序题，4==程序填空，5==
    public final String probTitle;//试题题目
    public final String probText;//试题内容
    public final int probScore;//程序题的空间大小，时间限制，总分值
    public String pid;//试题id
    public String probAns;//试题答案
    public String probSubject;//试题所属科目
    public String probTag;//试题考察知识点
    public int probSZ;
    public int probTM;


    // 如果传入的答案是空的，而且类型不是主观题，则从文件读取答案
    public Problem(int ptype, String pid, String ptitle, String ptext, String pans, int pscore) {
        this.probType = ptype;
        this.pid = pid;
        this.probTitle = ptitle;
        this.probText = ptext;
        this.probAns = pans;
        this.probScore = pscore;
        if (this.probType != 5 && this.probAns.isEmpty()) {
            this.probAns = getProbAns();
        }
    }

    public Problem(int ptype, String pid, String ptitle, String ptext, String pans, int pmaxsize, int pmaxtime, int pscore, String pSubject) {
        this.probType = ptype;
        this.pid = pid;
        this.probTitle = ptitle;
        this.probText = ptext;
        this.probAns = pans;
        this.probScore = pscore;
        this.probSZ = pmaxsize;
        this.probTM = pmaxtime;
        this.probSubject = pSubject;
        if (this.probType != 5 && this.probAns.isEmpty()) {
            this.probAns = getProbAns();
        }
    }

    public Problem(int ptype, String pid, String ptitle, String ptext, String pans, int pmaxsize, int pmaxtime, int pscore) {
        this.probType = ptype;
        this.pid = pid;
        this.probTitle = ptitle;
        this.probText = ptext;
        this.probAns = pans;
        this.probScore = pscore;
        this.probSZ = pmaxsize;
        this.probTM = pmaxtime;
        if (this.probType != 5 && this.probAns.isEmpty()) {
            this.probAns = getProbAns();
        }
    }

    private static boolean isfaild(@NotNull ResultSet rs) {
        boolean flag = true;
        try {
            while (rs.next()) {
                flag = false;
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
        logger.debug(flag ? "True" : "False");
        return flag;
    }

    @NotNull
    private static String readLineByLine(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (NoSuchFileException e) {
            logger.warn("Here's not a ans file: {}", filePath);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return contentBuilder.toString();
    }

    public String getProbData(@NotNull String keyName) {
        // only support the following keyname:
        // pid
        // ptitle
        // ptype
        // pscore
        if ("pid".equals(keyName)) {
            return String.valueOf(this.pid);
        }
        if ("ptitle".equals(keyName)) {
            return this.probTitle;
        }
        if ("ptype".equals(keyName)) {
            return String.valueOf(this.probType);
        }
        if ("pscore".equals(keyName)) {
            return String.valueOf(this.probScore);
        }
        return "";
    }

    protected boolean addPid(String NewValue) {
        // add pid, 新的Pid不会写入数据库，必须执行updateProb才能将Pid写入数据库
        try {
            DatabaseUtil.getConnection();
            PreparedStatement sta = DatabaseUtil.prepareStatement("select * from problem where pid=?");
            sta.setString(1, NewValue);
            logger.debug("[problemservice]: addPid: SQL: {}", sta);
            ResultSet queryResult = sta.executeQuery();
            if (isfaild(queryResult)) {
                this.pid = NewValue;
            } else {
                return true;
            }
            return false;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
        return true;
    }

    @NotNull
    private String getProbAns() {
        String ansFile = HOME_PATH + "/OnlineJudge/ans/" + this.pid + ".ans";
        logger.info(ansFile);
        String tmp = readLineByLine(ansFile);
        logger.info(tmp);
        return tmp;
        // ans路径：$HOME/OnlineJudge/ans/$Pid.ans
    }

    private boolean writeAnsFile() {
        // 将试题的Ans写入 "$HOME/OnlineJudge/ans/$Pid.ans"
        // 并将编程题答案进行处理，将输入数据（奇数行）写入：“$HOME/OnlineJudge/input/$Pid.in”
        if (this.probAns.isEmpty()) {
            return true;
        }
        File ansFile = new File(HOME_PATH + "/OnlineJudge/ans/" + this.pid + ".ans");
        // ans路径：$HOME/OnlineJudge/ans/$Pid.ans
        boolean result = true;
        FileWriter ansWrite = null;
        try {
            ansWrite = new FileWriter(ansFile);
            ansWrite.write(this.probAns);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            result = false;
        } finally {
            if (ansWrite != null) {
                try {
                    ansWrite.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return result;
    }

    protected boolean updateProb() {
        // 更新数据库中题目内容为当前题目内容
        // 更新成功返回true,失败返回false
        // 可以检测，当前题目是否存在在当前数据库中
        PreparedStatement sta;
        try {
            DatabaseUtil.getConnection();
            sta = DatabaseUtil.prepareStatement("select * from problem where pid = ?");
            sta.setString(1, this.pid);
            ResultSet queryResult = sta.executeQuery();
            if (isfaild(queryResult)) {
                // 当前试题为新添加试题，执行insert
                sta = DatabaseUtil.prepareStatement("insert into problem (ptitle, ptext, ptype, pscore, pmaxsize, pmaxtime, psubject, pid) " +
                        "values (?, ?, ?, ?, ?, ?, ?, ?)");
                logger.info("[problemservice]: {} - \n\tadding problem pid={}", this, this.pid);
            } else {
                //当前试题为试题内容更新，使用update
                sta = DatabaseUtil.prepareStatement("update problem set ptitle = ?, ptext = ?, ptype = ?, pscore = ?, " +
                        "pmaxsize = ?, pmaxtime = ?, psubject = ? where pid = ?");
                logger.info("[problemservice]: ProblemUpdate - \n\tupdating problem pid={}", this.pid);
            }
            sta.setString(1, this.probTitle);
            sta.setString(2, this.probText);
            sta.setInt(3, this.probType);
            sta.setInt(4, this.probScore);
            sta.setInt(5, this.probSZ);
            sta.setInt(6, this.probTM);
            sta.setString(7, this.probSubject);
            sta.setString(8, this.pid);
            if (!writeAnsFile()) {
                logger.warn("[problemservice]:{}: Write Ans file faild!", this);
                return false;
            }
            logger.info("[problemservice]:{} SQL:\n\t{}", this, sta);
            sta.executeUpdate();
            logger.info("[problemservice]: ProblemUpdate - done! \n\tpid={}", this.pid);

            queryResult.close();
            sta.close();
            DatabaseUtil.closeConnection();
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            DatabaseUtil.closeConnection();
            return false;
        }
    }
}

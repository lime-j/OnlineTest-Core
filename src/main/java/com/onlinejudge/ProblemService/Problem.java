package com.onlinejudge.ProblemService;

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

import static com.onlinejudge.DaemonService.DaemonServiceMain.debugPrint;
import static com.onlinejudge.util.DatabaseUtil.*;

public class Problem {
    final private String homePath = "/tmp";
    //    final private String homePath=System.getProperties().getProperty("user.home");
    public int ProbType; //试题类型，1==选择题，2==填空题，3==程序题，4==程序填空，5==
    public String Pid;//试题id
    public String ProbTile;//试题题目
    public String ProbText;//试题内容
    public String ProbAns;//试题答案
    public String ProbSubject;//试题所属科目
    public String ProbTag;//试题考察知识点
    public int ProbSZ, ProbTM, ProbScore;//程序题的空间大小，时间限制，总分值
    private Tag currTag;


    // 如果传入的答案是空的，而且类型不是主观题，则从文件读取答案
    public Problem(int ptype, String pid, String ptitle, String ptext, String pans, int pscore) {
        this.ProbType = ptype;
        this.Pid = pid;
        this.ProbTile = ptitle;
        this.ProbText = ptext;
        this.ProbAns = pans;
        this.ProbScore = pscore;
        if (this.ProbType != 5 && this.ProbAns.isEmpty()) {
            this.ProbAns = getProbAns();
        }
    }

    public Problem(int ptype, String pid, String ptitle, String ptext, String pans, int pmaxsize, int pmaxtime, int pscore, String pSubject, String pTag) {
        this.ProbType = ptype;
        this.Pid = pid;
        this.ProbTile = ptitle;
        this.ProbText = ptext;
        this.ProbAns = pans;
        this.ProbScore = pscore;
        this.ProbSZ = pmaxsize;
        this.ProbTM = pmaxtime;
        this.ProbSubject = pSubject;
        this.ProbTag = pTag;
        if (this.ProbType != 5 && this.ProbAns.isEmpty()) {
            this.ProbAns = getProbAns();
        }
        this.currTag = new Tag(pSubject, pTag);
    }

    public Problem(int ptype, String pid, String ptitle, String ptext, String pans, int pmaxsize, int pmaxtime, int pscore) {
        this.ProbType = ptype;
        this.Pid = pid;
        this.ProbTile = ptitle;
        this.ProbText = ptext;
        this.ProbAns = pans;
        this.ProbScore = pscore;
        this.ProbSZ = pmaxsize;
        this.ProbTM = pmaxtime;
        if (this.ProbType != 5 && this.ProbAns.isEmpty()) {
            this.ProbAns = getProbAns();
        }
    }

    private static boolean isfaild(ResultSet rs) {
        boolean flag = true;
        try {
            while (rs.next()) {
                flag = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(flag);
        return flag;
    }

    public String getProbData(String KeyName) {
        // only support the following keyname:
        // pid
        // ptitle
        // ptype
        // pscore
        if (KeyName.equals("pid")) {
            return String.valueOf(this.Pid);
        }
        if (KeyName.equals("ptitle")) {
            return this.ProbTile;
        }
        if (KeyName.equals("ptype")) {
            return String.valueOf(this.ProbType);
        }
        if (KeyName.equals("pscore")) {
            return String.valueOf(this.ProbScore);
        }
        return "";
    }

    protected boolean addPid(String NewValue) {
        // add pid, 新的Pid不会写入数据库，必须执行updateProb才能将Pid写入数据库
        try {
            if (this.currTag != null) {
                this.currTag.updateTag();
            }
            getConnection();
            PreparedStatement sta = prepareStatement("select * from problem where pid=?");
            sta.setString(1, NewValue);
            debugPrint("[ProblemService]: addPid: SQL: " + sta.toString());
            ResultSet QueryResult = sta.executeQuery();
            if (isfaild(QueryResult)) {
                this.Pid = NewValue;
            } else {
                return false;
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getProbAns() {
        String ansFile = this.homePath + "/OnlineJudge/ans/" + this.Pid + ".ans";
        debugPrint(ansFile);
        String tmp = readLineByLine(ansFile);
        debugPrint(tmp);
        return tmp;
        // ans路径：$HOME/OnlineJudge/ans/$Pid.ans
    }


    private static String readLineByLine(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (NoSuchFileException e) {
            debugPrint("[ProblemService]: Warning: Here's not a ans file: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contentBuilder.toString();
    }


    private boolean writeAnsFile() {
        // 将试题的Ans写入 "$HOME/OnlineJudge/ans/$Pid.ans"
        // 并将编程题答案进行处理，将输入数据（奇数行）写入：“$HOME/OnlineJudge/input/$Pid.in”
        if (this.ProbAns.isEmpty()) {
            return true;
        }
        File AnsFile = new File(this.homePath + "/OnlineJudge/ans/" + this.Pid + ".ans");
        // ans路径：$HOME/OnlineJudge/ans/$Pid.ans
        try {
            FileWriter AnsWrite = new FileWriter(AnsFile);
            AnsWrite.write(this.ProbAns);
            AnsWrite.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    protected boolean updateProb() {
        // 更新数据库中题目内容为当前题目内容
        // 更新成功返回true,失败返回false
        // 可以检测，当前题目是否存在在当前数据库中
        PreparedStatement sta = null;
        try {
            getConnection();
            sta = prepareStatement("select * from problem where pid = ?");
            sta.setString(1, this.Pid);
            ResultSet QueryResult = sta.executeQuery();
            if (isfaild(QueryResult)) {
                // 当前试题为新添加试题，执行insert
                sta = prepareStatement("insert into problem (ptitle, ptext, ptype, pscore, pmaxsize, pmaxtime, psubject, ptag, pid) " +
                        "values (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                debugPrint("[ProblemService]: " + this.toString() + " - \n\tadding problem pid=" + this.Pid);
            } else {
                //当前试题为试题内容更新，使用update
                sta = prepareStatement("update problem set ptitle = ?, ptext = ?, ptype = ?, pscore = ?, " +
                        "pmaxsize = ?, pmaxtime = ?, psubject = ?, ptag = ? where pid = ?");
                debugPrint("[ProblemService]: ProblemUpdate - \n\tupdating problem pid=" + this.Pid);
            }
            sta.setString(1, this.ProbTile);
            sta.setString(2, this.ProbText);
            sta.setInt(3, this.ProbType);
            sta.setInt(4, this.ProbScore);
            sta.setInt(5, this.ProbSZ);
            sta.setInt(6, this.ProbTM);
            sta.setString(7, this.ProbSubject);
            sta.setString(8, this.ProbTag);
            sta.setString(9, this.Pid);
            if (!writeAnsFile()) {
                debugPrint("[ProblemService]:" + this.toString() + ": Write Ans file faild!");
                return false;
            }
            debugPrint("[ProblemService]:" + this.toString() + " SQL:\n\t" + sta.toString());
            sta.executeUpdate();
            debugPrint(String.format("[ProblemService]: ProblemUpdate - done! \n\tpid=%s", this.Pid));

            QueryResult.close();
            sta.close();
            closeConnection();
//            closeQuery(QueryResult, sta, conn);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            closeConnection();
            return false;
        }
    }
}

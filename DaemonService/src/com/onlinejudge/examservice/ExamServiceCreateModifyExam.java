package com.onlinejudge.examservice;

import com.onlinejudge.util.BooleanEvent;
import com.onlinejudge.util.DatabaseUtil;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.onlinejudge.daemonservice.DaemonServiceMain.*;
import static com.onlinejudge.util.DatabaseUtil.*;

public class ExamServiceCreateModifyExam extends BooleanEvent {
    private Exam handling;

    public ExamServiceCreateModifyExam(Exam handling) {
        this.handling = handling;
    }

    public boolean go() {
        var examID = this.handling.getExamID();
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            debugPrint("ExamServiceCreateModifyExam, conn settled.");
            stmt = prepareStatement("select eid from exam where eid = ?");
            stmt.setString(1, this.handling.getExamID());
            var res = stmt.executeQuery();
            int cnt = 0;
            while (res.next()) ++cnt;
            stmt.close();
            debugPrint("ExamServiceCreateModifyExam, find " + cnt + " eid(s)");
            if (cnt == 0) {
                // 更新考试表当中的信息
                boolean flag = this.handling.getStartTime().contains("/");
                // 由于csharp的问题, 解析两种不同格式的日期信息
                SimpleDateFormat sdf = null;
                if (flag) {
                    sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                } else new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                assert (sdf != null);
                Timestamp stime = new Timestamp(sdf.parse(this.handling.getStartTime()).getTime());
                Timestamp etime = new Timestamp(sdf.parse(this.handling.getEndTime()).getTime());
                this.handling.setExamID(UUID.randomUUID().toString().replace("-", "").substring(24));
                examID = this.handling.getExamID();

                stmt = prepareStatement("insert into exam values ( ? , ? , ? , ? , ? , ? , ? )");
                stmt.setString(1, this.handling.getExamID());
                stmt.setString(2, this.handling.getExamName());
                stmt.setTimestamp(3, stime);
                stmt.setTimestamp(4, etime);
                stmt.setString(5, this.handling.getUserID());
                stmt.setString(6, this.handling.getExamText());
                stmt.setString(7, this.handling.getExamSubject());
                debugPrint("ExamServiceCreateModifyExam, " + stmt.toString());
                stmt.executeUpdate();
                stmt.close();


                stmt = prepareStatement("insert into examperm values ( ? , ? )");
                stmt.setString(1, examID);
                stmt.setString(2, this.handling.getUserID());
                debugPrint("ExamServiceCreateModifyExam, " + stmt.toString());
                stmt.executeUpdate();
                var lst = this.handling.getProblemList();
                stmt.close();
                stmt = prepareStatement("insert into examprob values ( ? , ? )");
                for (String str : lst) {
                    //qry = String.format("insert into examprob values ('%s','%s')", examID, str);
                    stmt.setString(1, examID);
                    stmt.setString(2, str);
                    stmt.executeUpdate();
                    debugPrint("ExamServiceCreateModifyExam, " + stmt.toString());
                }
            } else if (cnt == 1) {
                boolean flag = this.handling.getStartTime().contains("/");
                SimpleDateFormat sdf = null;
                if (flag) {
                    sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                } else new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                assert (sdf != null);
                Timestamp stime = new Timestamp(sdf.parse(this.handling.getStartTime()).getTime());
                Timestamp etime = new Timestamp(sdf.parse(this.handling.getEndTime()).getTime());

                stmt = prepareStatement("update exam set eName = ? ,estart =  ? ,eend = ? ,eowner = ? ,etext = ? ,esubject = ?  where eid = ?");
                stmt.setString(1, this.handling.getExamName());
                stmt.setTimestamp(2, stime);
                stmt.setTimestamp(3, etime);
                stmt.setString(4, this.handling.getUserID());
                stmt.setString(5, this.handling.getExamText());
                stmt.setString(6, this.handling.getExamSubject());
                stmt.setString(7, this.handling.getExamID());
                debugPrint("ExamServiceCreateModifyExam, " + stmt.toString());
                stmt.executeUpdate();
                stmt.close();


                String qry = String.format("select pid from examprob where eid = '%s'", examID);
                debugPrint("ExamServiceCreateModifyExam, " + qry);
                stmt = prepareStatement(qry);
                var ret = stmt.executeQuery();
                // 更新 考试 - 题目 表中的信息
                List<String> plst = new ArrayList<>();
                var lst = this.handling.getProblemList();
                int plstLen = 0;
                while (ret.next()) {
                    ++plstLen;
                    var tmp = ret.getString("pid");
                    plst.add(tmp);
                }
                List<String> toBeAdd = new ArrayList<>();
                List<String> toBeDelete = new ArrayList<>();
                // 在新的题目列表里但是不在旧的题目列表里边的 + toBeAdd
                for (String newpid : lst) {
                    boolean flg = false;
                    for (String pid : plst) {
                        if (pid.equals(newpid)) {
                            flg = true;
                            break;
                        }
                    }
                    if (!flg) toBeAdd.add(newpid);
                }
                // 在旧的题目列表而不再新的题目列表里边, + toBeDelete
                for (String pid : plst) {
                    boolean flg = false;
                    for (String newpid : lst) {
                        if (pid.equals(newpid)) {
                            flg = true;
                            break;
                        }
                    }
                    if (!flg) toBeDelete.add(pid);
                }
                // 检查两个列表有没有重复的元素, 如果有的话说明什么地方错误了, 应该报错.
                for (String a : toBeAdd) {
                    for (String b : toBeDelete) {
                        if (a.equals(b)) {
                            stmt.close();
                            closeConnection();
                            debugPrint("Shit! Something wrong inside the database!");
                            return false;
                        }
                    }
                }
                stmt.close();
                stmt = prepareStatement("insert into examprob values (?, ?)");
                for (String a : toBeAdd) {
                    stmt.setString(1, examID);
                    stmt.setString(2, a);
                    debugPrint("ExamServiceCreateModifyExam, " + stmt.toString());
                    stmt.execute();
                }
                stmt.close();

                stmt = prepareStatement("delete from examprob where eid =  ? and pid = ? ");
                for (String a : toBeDelete) {
                    stmt.setString(1, examID);
                    stmt.setString(2, a);
                    debugPrint("ExamServiceCreateModifyExam, " + stmt.toString());
                    stmt.execute();
                }
                DatabaseUtil.closeQuery(ret, stmt, conn);

                // 每次更新考试，都会检查所有者是否在examPerm表中存在
                this.handling.updateExamPerm();
            } else {
                DatabaseUtil.closeUpdate(stmt, conn);
                return false;
            }
        } catch (SQLException | ParseException sql) {
            sql.printStackTrace();
            try {
                DatabaseUtil.closeUpdate(stmt, conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }
}

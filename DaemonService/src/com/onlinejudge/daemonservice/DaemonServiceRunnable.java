package com.onlinejudge.daemonservice;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.onlinejudge.examservice.*;
import com.onlinejudge.managementservice.ManagementServiceAddListUsers;
import com.onlinejudge.managementservice.ManagerServiceTeacherSubject;
import com.onlinejudge.problemservice.*;
import com.onlinejudge.searchservice.SearchServiceDoQuery;
import com.onlinejudge.userservice.*;
import com.onlinejudge.util.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static com.onlinejudge.daemonservice.DaemonServiceMain.DBG;

public class DaemonServiceRunnable implements Runnable {
    private Socket cl;

    DaemonServiceRunnable(Socket sc) {
        this.cl = sc;
    }

    public void debugPrint(String str) {
        System.out.println(DBG + ", " + Thread.currentThread().toString() + " ," + str);
    }

    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.cl.getInputStream()));
            String recv = bufferedReader.readLine();
            debugPrint("deamonservice, " + recv);

            var jsonObject = JSON.parseObject(recv);
            // 这些是基本元素, 是一个请求必须发送的, 如果请求没含有这些内容, 就是不合法的
            String requestType = jsonObject.getString("requestType");
            String userID = jsonObject.getString("userID");
            String userToken = jsonObject.getString("userToken");
            var checkToken = new UserServiceCheckToken(userID, userToken);
            //
            // 检查Token的合法性, 验证这个用户是否有权限进行操作.
            //
            boolean checkResult = checkToken.go();
            if (!checkResult) {
                throw new TokenWrongException();
            }
            Handler handler = null;
            try {
                switch (requestType) {
                    case "queryStudentPracticeScore":
                        handler = new IntegerEventHandler(
                                new ExamServiceQueryStudentScore(
                                        jsonObject.getString("examID"),
                                        jsonObject.getString("queryUserID"), 2
                                )
                        );
                        break;
                    case "deleteExam":
                        handler = new BooleanEventHandler(
                                new ExamServiceDeleteExam(jsonObject.getString("examID"))
                        );
                        break;
                    case "listExamStudent":
                        handler = new ListEventHandler(
                                new ExamServiceListExamStudent(
                                        jsonObject.getString("examID")
                                )
                        );
                        break;
                    // UserServiceEvent
                    case "adminModifySex":
                        handler = new BooleanEventHandler(
                                new UserServiceUpdateProperties(
                                        jsonObject.getString("changeUserID"), Integer.toString(jsonObject.getIntValue("newSex")), 1
                                )
                        );
                        break;
                    case "adminUpdateUserName":
                        handler = new BooleanEventHandler(
                                new UserServiceUpdateProperties(
                                        jsonObject.getString("changeUserID"), jsonObject.getString("newUserName"), 2
                                )
                        );
                        break;
                    case "adminResetPassword":
                        handler = new BooleanEventHandler(
                                new UserServiceUpdateProperties(
                                        jsonObject.getString("changeUserID"), "670b14728ad9902aecba32e22fa4f6bd", 3
                                )
                        );
                        break;
                    case "modifySex":
                        handler = new BooleanEventHandler(
                                new UserServiceUpdateProperties(
                                        userID, Integer.toString(jsonObject.getIntValue("newSex")), 1
                                )
                        );
                        break;
                    case "updateUserName":
                        handler = new BooleanEventHandler(
                                new UserServiceUpdateProperties(
                                        userID, jsonObject.getString("newUserName"), 2
                                )
                        );
                        break;
                    case "updatePassword":
                        handler = new BooleanEventHandler(
                                new UserServiceUpdateProperties(
                                        userID, jsonObject.getString("newPassword"), 3
                                )
                        );
                        break;
                    case "deleteAccount":
                        handler = new BooleanEventHandler(
                                new UserServiceDeleteAccount(
                                        jsonObject.getString("deleteUserID")
                                )
                        );
                        break;
                    case "listAllUser":
                        handler = new ListEventHandler(new UserServiceListAllUser());
                        break;
                    // SearchService
                    case "searchContest":
                        handler = new ListEventHandler(
                                new SearchServiceDoQuery(
                                        userID, 2, jsonObject.getString("keyword")
                                )
                        );
                        break;
                    case "searchProblem":
                        handler = new ListEventHandler(
                                new SearchServiceDoQuery(
                                        userID, 1, jsonObject.getString("keyword")
                                )
                        );
                        break;
                    // ExamService
                    case "createModifyExam":
                        handler = new BooleanEventHandler(
                                new ExamServiceCreateModifyExam(
                                        new Exam(
                                                jsonObject.getString("examID"),
                                                jsonObject.getString("examName"),
                                                jsonObject.getString("userID"),
                                                jsonObject.getString("startTime"),
                                                jsonObject.getString("endTime"),
                                                jsonObject.getString("ExamText"),
                                                jsonObject.getString("subject"),
                                                JSONObject.parseArray(jsonObject.getJSONArray("problemList").toJSONString(), String.class)
                                        )));
                        break;
                    case "deleteProblemFromExam":
                        handler = new BooleanEventHandler(new ExamServiceDeleteProblemFromExam(
                                jsonObject.getString("examID"),
                                jsonObject.getString("problemID")
                        ));
                        break;
                    case "listExam":
                        handler = new ListEventHandler(
                                new ExamServiceListExam(
                                        jsonObject.getString("userID")
                                )
                        );
                        break;
                    case "listExamProblem":
                        handler = new ListEventHandler(
                                new ExamServiceListExamProblem(
                                        jsonObject.getString("examID")
                                )
                        );
                        break;
                    case "queryStudentScore":
                        handler = new IntegerEventHandler(new ExamServiceQueryStudentScore(
                                jsonObject.getString("examID"),
                                jsonObject.getString("queryUserID"), 1
                        ));
                        break;
                    case "replaceProblemFromExam":
                        handler = new ListEventHandler(new ExamServiceReplaceProblem(
                                jsonObject.getString("examID"), jsonObject.getString("newID")
                                , jsonObject.getString("oldID")
                        ));
                        break;

                    // ProblemService
                    case "listProblemFromDatabase":
                        handler = new ListEventHandler(
                                new ProblemServiceListAllProblem(
                                        jsonObject.getString("subject"), JSONObject.parseArray(jsonObject.getJSONArray("tagList").toJSONString(), String.class)
                                )
                        );
                        break;
                    case "listSubject":
                        handler = new ListEventHandler(
                                new UserServiceListSubject(
                                        jsonObject.getString("teacherID")
                                )
                        );
                        break;
                    case "listTag":
                        handler = new ListEventHandler(
                                new ProblemServiceListTag(
                                        jsonObject.getString("subject")
                                )
                        );
                        break;
                    case "problemUpdate":
                        handler = new BooleanEventHandler(
                                new ProblemServiceCreateProblem(
                                        new Problem(
                                                jsonObject.getIntValue("proType"),
                                                jsonObject.getString("proID"),
                                                jsonObject.getString("proTitle"),
                                                jsonObject.getString("proText"),
                                                jsonObject.getString("proAns"),
                                                jsonObject.getIntValue("proMaxsize"),
                                                jsonObject.getIntValue("proMaxtime"),
                                                jsonObject.getIntValue("proScore"),
                                                jsonObject.getString("proSubject"),
                                                jsonObject.getString("proTag")
                                        )));
                        break;
                    case "subUpdate":
                        handler = new BooleanEventHandler(
                                new ProblemSubmissionToQueue(
                                        new Submission(
                                                jsonObject.getString("subText"),
                                                jsonObject.getString("subUserID"),
                                                jsonObject.getString("subProb"),
                                                jsonObject.getString("subExam")
                                        )));
                        break;
                    case "antiCheatListSubmission":
                        handler = new ListEventHandler(
                                new ProblemServiceAntiCheatListSubmission(
                                        jsonObject.getString("examID"), jsonObject.getString("probID")
                                )
                        );
                        break;
                    case "subjectSubList":
                        handler = new ListEventHandler(
                                new ProblemServiceSubjectiveSubReturn(
                                        jsonObject.getString("examID"), jsonObject.getString("probID")
                                )
                        );
                        break;
                    case "changeScore":
                        handler = new BooleanEventHandler(new ProblemServiceChangeScore(
                                jsonObject.getString("subID"), jsonObject.getIntValue("score")
                        ));
                        break;
                    case "addListUser":
                        handler = new BooleanEventHandler(new ManagementServiceAddListUsers(
                                JSONObject.parseArray(jsonObject.getJSONArray("userlist").toJSONString(), String.class)
                        ));
                        break;
                    case "editProblemFromExam":
                        handler = new StringEventHandler(new ProblemServiceNewInvisibleProblem(new InvisibleProblem(
                                jsonObject.getIntValue("proType"), jsonObject.getString("proID"), jsonObject.getString("proTitle")
                                , jsonObject.getString("proText"), jsonObject.getString("proAns"), jsonObject.getIntValue("proMaxsize")
                                , jsonObject.getIntValue("proMaxtime"), jsonObject.getIntValue("proScore"), jsonObject.getString("examID")
                                , jsonObject.getString("proSubject"), jsonObject.getString("proTag")
                        )));
                        break;
                    case "deleteProblemFromDatabase":
                        handler = new BooleanEventHandler(new ProblemServiceDeleteProblem(jsonObject.getString("proID")));
                        break;
                    case "createProblemList":
                        handler = new ListEventHandler(new ProblemServiceCreateProblemList(jsonObject.getString("subject"),
                                JSONObject.parseArray(jsonObject.getJSONArray("tagList").toJSONString(), String.class),
                                jsonObject.getIntValue("choice"), jsonObject.getIntValue("TorF"), jsonObject.getIntValue("blank"),
                                jsonObject.getIntValue("subjective"), jsonObject.getIntValue("programBlank"),
                                jsonObject.getIntValue("program")));
                        break;
                    case "createProblemTag":
                        handler = new BooleanEventHandler(new ProblemServiceCreateTag(new Tag(
                                jsonObject.getString("subject"), jsonObject.getString("newTag")
                        )));
                        break;
                    // ManagerService
                    case "addUserIntoExam":
                        handler = new BooleanEventHandler(new ExamServiceAddUserIntoExam(
                                JSONObject.parseArray(jsonObject.getJSONArray("userList").toJSONString(), String.class),
                                jsonObject.getString("examID")
                        ));
                        break;
                    case "addTeacherSubject":
                        handler = new BooleanEventHandler(new ManagerServiceTeacherSubject(
                                JSONObject.parseArray(jsonObject.getJSONArray("tsList").toJSONString(), String.class)
                        ));
                        break;
                    case "Fuck off, this hasn't been done yet":
                        break;
                    default:
                        handler = new NothingToDoHandler();
                        System.out.println("hi, nmsl!");
                        break;
                }
            } catch (JSONException e) {
                System.out.println("Fuck you, something wrong with your JSON!");
                throw e;
            }
            assert handler != null;
            debugPrint(handler.result);
            this.cl.getOutputStream().write(handler.result.getBytes(StandardCharsets.UTF_8));
            Thread.sleep(50);
            this.cl.close();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                this.cl.getOutputStream().write("{\"status\":-1}".getBytes(StandardCharsets.UTF_8));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                this.cl.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (TokenWrongException wa) {
            System.out.println("The token is wrong.");
            try {
                this.cl.getOutputStream().write("{\"status\":-2}".getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                this.cl.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            wa.printStackTrace();
        } catch (NullPointerException eee) {
            System.out.println("Client's request is invalid, please .");
            try {
                this.cl.getOutputStream().write("{\"status\":-3}".getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                this.cl.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            eee.printStackTrace();
        } catch (Exception ee) {
            System.out.println("Something else is wrong.");
            try {
                this.cl.getOutputStream().write("{\"status\":-1}".getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                this.cl.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ee.printStackTrace();
        }
    }
}

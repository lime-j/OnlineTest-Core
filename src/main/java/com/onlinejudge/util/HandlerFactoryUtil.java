package com.onlinejudge.util;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.onlinejudge.examservice.Exam;
import com.onlinejudge.examservice.ExamServiceCreateModifyExam;
import com.onlinejudge.examservice.ExamServiceDeleteExam;
import com.onlinejudge.examservice.ExamServiceDeleteProblemFromExam;
import com.onlinejudge.examservice.ExamServiceListContest;
import com.onlinejudge.examservice.ExamServiceListExamProblem;
import com.onlinejudge.examservice.ExamServiceListExamStudent;
import com.onlinejudge.examservice.ExamServiceQueryStudentScore;
import com.onlinejudge.examservice.ExamServiceReplaceProblem;
import com.onlinejudge.loginservice.LoginCheck;
import com.onlinejudge.loginservice.LoginServiceChangePassword;
import com.onlinejudge.loginservice.LoginServiceSendMail;
import com.onlinejudge.manservice.ManServiceAddListUsers;
import com.onlinejudge.problemservice.Problem;
import com.onlinejudge.problemservice.ProblemServiceChangeScore;
import com.onlinejudge.problemservice.ProblemServiceCreateProblem;
import com.onlinejudge.problemservice.ProblemServiceDeleteProblem;
import com.onlinejudge.problemservice.ProblemServiceListAllProblem;
import com.onlinejudge.problemservice.ProblemServiceListAllSubject;
import com.onlinejudge.problemservice.ProblemServiceListSubmission;
import com.onlinejudge.problemservice.ProblemServiceSetSubject;
import com.onlinejudge.problemservice.ProblemServiceSubjectiveSubReturn;
import com.onlinejudge.problemservice.ProblemSubmissionToQueue;
import com.onlinejudge.problemservice.Submission;
import com.onlinejudge.searchservice.SearchServiceDoQuery;
import com.onlinejudge.userservice.UserServiceDeleteAccount;
import com.onlinejudge.userservice.UserServiceGetTimeLine;
import com.onlinejudge.userservice.UserServiceListAllUser;
import com.onlinejudge.userservice.UserServiceListTimeline;
import com.onlinejudge.userservice.UserServiceUpdateProperties;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.alibaba.fastjson.JSON.parseArray;

public class HandlerFactoryUtil {
    private static final Logger logger = LoggerFactory.getLogger(HandlerFactoryUtil.class);
    private static final Map<String, HandlerEnum> mp = new HashMap<>();
    private static final String USERID = "userID";
    @Contract(pure = true)
    private HandlerFactoryUtil() {
    }

    static {
        for (var it : HandlerEnum.values()) {
            mp.put(it.name, it);
        }
    }

    public static Handler getHandler(@NotNull String requestType, JSONObject jsonObject) throws InternalException {
        Handler handler = null;
        HandlerEnum requestEnum = mp.get(requestType);
        try {
            switch (requestEnum) {
                case listTimeline:
                    handler = new ListEventHandler(
                            new UserServiceListTimeline(jsonObject.getString(USERID))
                    );
                    break;
                case getTimeline:
                    handler = new ListEventHandler(
                            new UserServiceGetTimeLine(
                                    jsonObject.getString(USERID)
                            )
                    );
                    break;
                case listSubject:
                    handler = new ListEventHandler(
                            new ProblemServiceListAllSubject()
                    );
                    break;
                case setSubject:
                    handler = new BooleanEventHandler(new ProblemServiceSetSubject(
                            parseArray(jsonObject.getJSONArray("subject").toJSONString(), String.class), jsonObject.getString(USERID)
                    ));
                    break;
                case sendMail:
                    handler = new BooleanEventHandler(
                            new LoginServiceSendMail(
                                    jsonObject.getString(USERID)
                            )
                    );
                    break;
                case changePassword:
                    handler = new BooleanEventHandler(
                            new LoginServiceChangePassword(
                                    jsonObject.getString(USERID),
                                    jsonObject.getString("userKey"),
                                    jsonObject.getString("newPassword")
                            )
                    );
                    break;
                case login:
                    handler = new ClassEventHandler(
                            new LoginCheck(
                                    jsonObject.getString("userPassword"),
                                    jsonObject.getString(USERID),
                                    jsonObject.getString("userToken")
                            )
                    );
                    break;
                case queryStudentPracticeScore:
                    handler = new IntegerEventHandler(
                            new ExamServiceQueryStudentScore(
                                    jsonObject.getString("examID"),
                                    jsonObject.getString("queryUserID"), 2
                            )
                    );
                    break;
                case deleteExam:
                    handler = new BooleanEventHandler(
                            new ExamServiceDeleteExam(jsonObject.getString("examID"))
                    );
                    break;
                case listExamStudent:
                    handler = new ListEventHandler(
                            new ExamServiceListExamStudent(
                                    jsonObject.getString("examID")
                            )
                    );
                    break;
                // UserServiceEvent
                case adminModifySex:
                    handler = new BooleanEventHandler(
                            new UserServiceUpdateProperties(
                                    jsonObject.getString("changeUserID"), Integer.toString(jsonObject.getIntValue("newSex")), 1
                            )
                    );
                    break;
                case adminUpdateUserName:
                    handler = new BooleanEventHandler(
                            new UserServiceUpdateProperties(
                                    jsonObject.getString(USERID), jsonObject.getString("userName"), 2
                            )
                    );
                    break;
                case adminResetPassword:
                    handler = new BooleanEventHandler(
                            new UserServiceUpdateProperties(
                                    jsonObject.getString("changeUserID"), "670b14728ad9902aecba32e22fa4f6bd", 3
                            )
                    );
                    break;
                case modifySex:
                    handler = new BooleanEventHandler(
                            new UserServiceUpdateProperties(
                                    jsonObject.getString(USERID), Integer.toString(jsonObject.getIntValue("newSex")), 1
                            )
                    );
                    break;
                case updatePassword:
                    handler = new BooleanEventHandler(
                            new UserServiceUpdateProperties(
                                    jsonObject.getString(USERID), jsonObject.getString("newPassword"), 3
                            )
                    );
                    break;
                case createProblemTag:
                    break;
                case deleteAccount:
                    handler = new BooleanEventHandler(
                            new UserServiceDeleteAccount(
                                    jsonObject.getString("deleteUserID")
                            )
                    );
                    break;
                case listAllUser:
                    handler = new ListEventHandler(new UserServiceListAllUser());
                    break;
                // searchservice
                case searchContest:
                    handler = new ListEventHandler(
                            new SearchServiceDoQuery(
                                    jsonObject.getString(USERID), 2, jsonObject.getString("keyword")
                            )
                    );
                    break;
                case searchProblem:
                    handler = new ListEventHandler(
                            new SearchServiceDoQuery(
                                    jsonObject.getString(USERID), 1, jsonObject.getString("keyword")
                            )
                    );
                    break;
                // examservice
                case createModifyExam:
                    handler = new BooleanEventHandler(
                            new ExamServiceCreateModifyExam(
                                    new Exam(
                                            jsonObject.getString("examID"),
                                            jsonObject.getString("examName"),
                                            jsonObject.getString(USERID),
                                            jsonObject.getString("startTime"),
                                            jsonObject.getString("endTime"),
                                            jsonObject.getString("ExamText"),
                                            jsonObject.getString("subject"),
                                            parseArray(jsonObject.getJSONArray("problemList").toJSONString(), String.class)
                                    )));
                    break;
                case deleteProblemFromExam:
                    handler = new BooleanEventHandler(new ExamServiceDeleteProblemFromExam(
                            jsonObject.getString("examID"),
                            jsonObject.getString("problemID")
                    ));
                    break;
                case listContest:
                    handler = new ListEventHandler(
                            new ExamServiceListContest(
                                    jsonObject.getString(USERID), jsonObject.getInteger("listType")

                            )
                    );
                    break;
                case listExamProblem:
                    handler = new ListEventHandler(
                            new ExamServiceListExamProblem(
                                    jsonObject.getString("examID")
                            )
                    );
                    break;
                case queryStudentScore:
                    handler = new IntegerEventHandler(new ExamServiceQueryStudentScore(
                            jsonObject.getString("examID"),
                            jsonObject.getString("queryUserID"), 1
                    ));
                    break;
                case replaceProblemFromExam:
                    handler = new ListEventHandler(new ExamServiceReplaceProblem(
                            jsonObject.getString("examID"), jsonObject.getString("newID")
                            , jsonObject.getString("oldID")
                    ));
                    break;

                // problemservice
                case listProblemFromDatabase:
                    handler = new ListEventHandler(
                            new ProblemServiceListAllProblem(
                                    jsonObject.getString("subject"), parseArray(jsonObject.getJSONArray("tagList").toJSONString(), String.class)
                            )
                    );
                    break;
                case problemUpdate:
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
                                            jsonObject.getString("proSubject")
                                    )));
                    break;
                case subUpdate:
                    handler = new BooleanEventHandler(
                            new ProblemSubmissionToQueue(
                                    new Submission(
                                            jsonObject.getString("subText"),
                                            jsonObject.getString("subUserID"),
                                            jsonObject.getString("subProb"),
                                            jsonObject.getString("subExam")
                                    )));
                    break;
                case subjectSubList:
                    handler = new ListEventHandler(
                            new ProblemServiceSubjectiveSubReturn(
                                    jsonObject.getString("examID"), jsonObject.getString("probID")
                            )
                    );
                    break;
                case changeScore:
                    handler = new BooleanEventHandler(new ProblemServiceChangeScore(
                            jsonObject.getString("subID"), jsonObject.getIntValue("score")
                    ));
                    break;
                case addListUser:
                    handler = new BooleanEventHandler(new ManServiceAddListUsers(
                            parseArray(jsonObject.getJSONArray("userlist").toJSONString(), String.class)
                    ));
                    break;
                case deleteProblemFromDatabase:
                    handler = new BooleanEventHandler(new ProblemServiceDeleteProblem(jsonObject.getString("proID")));
                    break;
                case listSubmission:
                    handler = new ListEventHandler(new ProblemServiceListSubmission(jsonObject.getString("examID"),
                            jsonObject.getString("probID"), jsonObject.getString("userID")
                    ));
                    break;
                default:
                    handler = new NothingToDoHandler();
                    logger.warn("Undefined behavior.");
                    break;
            }
        } catch (JSONException e) {
            logger.error("Fuck you, something wrong with your JSON!", e);
        }
        return handler;
    }
}

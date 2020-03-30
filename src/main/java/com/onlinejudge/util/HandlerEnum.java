package com.onlinejudge.util;

public enum HandlerEnum {
    addListUser("addListUser"),
    adminModifySex("adminModifySex"),
    adminResetPassword("adminResetPassword"),
    adminUpdateUserName("adminUpdateUserName"),
    changePassword("changePassword"),
    changeScore("changeScore"),
    createModifyExam("createModifyExam"),
    createProblemList("createProblemList"),
    createProblemTag("createProblemTag"),
    deleteAccount("deleteAccount"),
    deleteExam("deleteExam"),
    deleteProblemFromDatabase("deleteProblemFromDatabase"),
    deleteProblemFromExam("deleteProblemFromExam"),
    editProblemFromExam("editProblemFromExam"),
    listAllUser("listAllUser"),
    listExam("listExam"),
    listExamProblem("listExamProblem"),
    listExamStudent("listExamStudent"),
    listProblemFromDatabase("listProblemFromDatabase"),
    listSubject("listSubject"),
    listTag("listTag"),
    login("login"),
    modifySex("modifySex"),
    problemUpdate("problemUpdate"),
    queryStudentPracticeScore("queryStudentPracticeScore"),
    queryStudentScore("queryStudentScore"),
    replaceProblemFromExam("replaceProblemFromExam"),
    searchContest("searchContest"),
    searchProblem("searchProblem"),
    sendMail("sendMail"),
    setSubject("setSubject"),
    subUpdate("subUpdate"),
    subjectSubList("subjectSubList"),
    updatePassword("updatePassword"),
    updateUserName("updateUserName"),
    getTimeline("getTimeline");
    public final String name;

    HandlerEnum(String name) {
        this.name = name;
    }
}

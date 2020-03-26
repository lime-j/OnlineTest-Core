package com.onlinejudge.util;

public enum HandlerEnum {
    login("login"),
    queryStudentPracticeScore("queryStudentPracticeScore"),
    deleteExam("deleteExam"),
    listExamStudent("listExamStudent"),
    adminModifySex("adminModifySex"),
    adminUpdateUserName("adminUpdateUserName"),
    adminResetPassword("adminResetPassword"),
    modifySex("modifySex"),
    updateUserName("updateUserName"),
    updatePassword("updatePassword"),
    deleteAccount("deleteAccount"),
    listAllUser("listAllUser"),
    searchContest("searchContest"),
    searchProblem("searchProblem"),
    createModifyExam("createModifyExam"),
    deleteProblemFromExam("deleteProblemFromExam"),
    listExam("listExam"),
    listExamProblem("listExamProblem"),
    queryStudentScore("queryStudentScore"),
    replaceProblemFromExam("replaceProblemFromExam"),
    listProblemFromDatabase("listProblemFromDatabase"),
    listSubject("listSubject"),
    listTag("listTag"),
    problemUpdate("problemUpdate"),
    subUpdate("subUpdate"),
    subjectSubList("subjectSubList"),
    changeScore("changeScore"),
    addListUser("addListUser"),
    editProblemFromExam("editProblemFromExam"),
    deleteProblemFromDatabase("deleteProblemFromDatabase"),
    createProblemList("createProblemList"),
    createProblemTag("createProblemTag"),
    changePassword("changePassword"),
    sendMail("sendMail");
    public String name;

    HandlerEnum(String name) {
        this.name = name;
    }
}

package com.onlinejudge.examservice;

import com.onlinejudge.problemservice.Problem;
import com.onlinejudge.util.ListEvent;

import java.util.List;

public class ExamServiceListExamProblem implements ListEvent<Problem> {
    private final String examID;

    public ExamServiceListExamProblem(String examID) {
        this.examID = examID;
    }

    @Override
    public void beforeGo() {
        // do nothing
    }

    @Override
    public void afterGo() {
        // do nothing
    }

    public List<Problem> go() {
        return ExamServiceGetProbList.getItem(examID);
    }
}

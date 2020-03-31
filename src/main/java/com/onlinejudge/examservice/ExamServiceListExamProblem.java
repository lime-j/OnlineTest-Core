package com.onlinejudge.examservice;

import com.onlinejudge.problemservice.Problem;
import com.onlinejudge.util.ListEvent;

import java.util.List;

public class ExamServiceListExamProblem extends ListEvent<Problem> {
    private final String examID;
    public ExamServiceListExamProblem(String examID) {
        this.examID = examID;
    }

    public List<Problem> go() {
        return ExamServiceGetProbList.getItem(examID);
    }
}

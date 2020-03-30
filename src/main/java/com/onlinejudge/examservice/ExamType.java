package com.onlinejudge.examservice;

public enum ExamType {
    CONTEST(1), COURSE(0);
    protected int type;
    ExamType(int type) {
        this.type = type;
    }
}

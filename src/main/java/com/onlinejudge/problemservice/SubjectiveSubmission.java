package com.onlinejudge.problemservice;

public class SubjectiveSubmission extends Submission {

    private final int ProbScore;

    SubjectiveSubmission(String Sid, String SubText, String SubUser, String SubProb, String SubExam, int ProbScore) {
        // 主观题提交类型
        // 只用在了主观题给分上
        super(Sid, SubText, SubUser, SubProb, SubExam);
        this.ProbScore = ProbScore;
    }

    public int getProbScore() {
        return this.ProbScore;
    }
}

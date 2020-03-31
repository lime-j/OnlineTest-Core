package com.onlinejudge.problemservice;

import com.onlinejudge.util.BooleanEvent;

public class ProblemServiceCreateTag implements BooleanEvent {
    // 创建知识点
    // requestType：createProblemTag
    private final Tag currTag;

    @Override
    public void beforeGo() {
        // do nothing
    }

    @Override
    public void afterGo() {
        // do nothing
    }

    public ProblemServiceCreateTag(Tag origion) {
        this.currTag = origion;
    }

    @Override
    public boolean go() {
        return this.currTag.updateTag();
    }
}

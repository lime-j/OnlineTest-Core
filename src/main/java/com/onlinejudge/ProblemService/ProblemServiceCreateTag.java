package com.onlinejudge.problemservice;

import com.onlinejudge.util.BooleanEvent;

public class ProblemServiceCreateTag extends BooleanEvent {
    // 创建知识点
    // requestType：createProblemTag
    private Tag currTag;

    public ProblemServiceCreateTag(Tag origion) {
        this.currTag = origion;
    }

    @Override
    public boolean go() {
        return this.currTag.updateTag();
    }
}

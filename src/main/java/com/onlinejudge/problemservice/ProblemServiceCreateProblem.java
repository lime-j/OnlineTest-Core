package com.onlinejudge.problemservice;


import com.onlinejudge.util.BooleanEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class ProblemServiceCreateProblem implements BooleanEvent {
    private final Problem currentProblem;
    private static Logger logger = LoggerFactory.getLogger(ProblemServiceCreateProblem.class);

    ////////////////////////////////////////////////////////////////////////////////////
    //更新（包括新建）请传送Problem类型的试题（所有内容，包括Pid（即试题id），如果为新插入试题，请令试题id为空字符串）
    ///////////////////////////////////////////////////////////////////////////////////


    public ProblemServiceCreateProblem(Problem CurrProb) {
        //用于创建(更新)问题
        // type: problemUpdate
        this.currentProblem = CurrProb;
    }

    @Override
    public void beforeGo() {
        // do nothing
    }

    @Override
    public void afterGo() {
        // do nothing
    }

    public boolean go() {
        if (this.currentProblem.getProbData("pid").isEmpty()) {
            UUID pid = UUID.randomUUID();
            while (this.currentProblem.addPid(pid.toString().replace('-', 'a').substring(0, 6))) {
                pid = UUID.randomUUID();
            }
            System.out.println(String.format("Problem Pid: %s", this.currentProblem.pid));
        }
        this.currentProblem.updateProb();
        return true;
    }
}
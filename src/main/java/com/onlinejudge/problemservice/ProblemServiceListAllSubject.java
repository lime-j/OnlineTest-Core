package com.onlinejudge.problemservice;

import com.onlinejudge.util.ListEvent;
import com.onlinejudge.util.StringListerUtil;

import java.util.List;

public class ProblemServiceListAllSubject extends ListEvent {
    public List<String> go() {
        return (new StringListerUtil(
                "select * from subject", "subject", this.toString()
        )).getResultList();
    }
}

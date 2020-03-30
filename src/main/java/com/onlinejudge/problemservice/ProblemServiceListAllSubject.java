package com.onlinejudge.problemservice;

import com.onlinejudge.util.ListEvent;
import com.onlinejudge.util.StringListerUtil;

import java.util.ArrayList;
import java.util.List;

public class ProblemServiceListAllSubject extends ListEvent<String> {
    private static final String CONTEST_STR = "contest";
    public List<String> go() {
        var lst = (new StringListerUtil(
                "select * from subject", "subject", this.toString()
        )).getResultList();
        List<String> ret = new ArrayList<>();
        for (var str : lst) {
            if (!CONTEST_STR.equals(str)) ret.add(str);
        }
        return ret;
    }
}

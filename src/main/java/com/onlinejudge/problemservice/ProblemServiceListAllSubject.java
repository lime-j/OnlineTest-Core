package com.onlinejudge.problemservice;

import com.onlinejudge.util.ListEvent;
import com.onlinejudge.util.ListerUtil;

import java.util.ArrayList;
import java.util.List;

public class ProblemServiceListAllSubject implements ListEvent<String> {
    private static final String CONTEST_STR = "contest";

    @Override
    public void beforeGo() {
        // do nothing
    }

    @Override
    public void afterGo() {
        // do nothing
    }

    public List<String> go() {
        var lst = (new ListerUtil<String>(
                "select * from subject", "subject", this.toString()
        )).getResultList();
        List<String> ret = new ArrayList<>();
        for (var str : lst) {
            if (!CONTEST_STR.equals(str)) ret.add(str);
        }
        return ret;
    }
}

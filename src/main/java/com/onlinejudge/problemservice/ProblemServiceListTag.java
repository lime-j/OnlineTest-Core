package com.onlinejudge.problemservice;

import com.onlinejudge.util.ListEvent;
import com.onlinejudge.util.ListerUtil;

import java.util.ArrayList;
import java.util.List;

public class ProblemServiceListTag extends ListEvent<String> {
    private final String subject;

    public ProblemServiceListTag(String subject) {
        this.subject = subject;
    }

    public List<String> go() {
        List<String> resultList = new ArrayList<>();
        try {
            return (new ListerUtil<String>(
                    "select * from subjecttag where subject = ?", "tag", this.subject, this.toString()
            )).getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}

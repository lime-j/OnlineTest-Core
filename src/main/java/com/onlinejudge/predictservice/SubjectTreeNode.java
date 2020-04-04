package com.onlinejudge.predictservice;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Log4j2
public class SubjectTreeNode {
    private String examID;
    private List<Edge> to;
    public SubjectTreeNode(@NotNull String examID){
        this.examID = examID;
        this.to = new ArrayList<>();
    }
    public void addEdge(@NotNull Edge edge){
        to.add(edge);
    }
}

package com.onlinejudge.predictservice;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class Edge {
    private String to;
    private double weight;
    public Edge(@NotNull String to, double weight){
        this.to = to;
        this.weight = weight;
    }
}

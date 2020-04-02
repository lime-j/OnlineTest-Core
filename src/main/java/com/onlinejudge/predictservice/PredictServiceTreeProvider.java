package com.onlinejudge.predictservice;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PredictServiceTreeProvider {
    // Happy tree friend!
    // (but this is actually a DAG, not tree!
    @NotNull
    @Contract(pure = true)
    private static List<Pair<String, Double>> getItem(){
        List<Pair<String, Double>> result = new ArrayList<>();
        result.add(new ImmutablePair<>("114514",1919.810));
        return result;
    }
    private static final Map<String, String> TO = new HashMap<>();
    private static final Map<String, Edge> EDGE = new HashMap<>();
    public PredictServiceTreeProvider(){
        TO.put("114514","114514");
        EDGE.put("114514",new Edge("114514",1));
    }
    private static List<Pair<String, Double>> bfs(String root){
        List<Pair<String, Double>> result = new ArrayList<>();
        Queue<Edge> que = new ArrayDeque<>();
        que.remove();
        while(!que.isEmpty()){
            var top = que.peek();
            que.remove();
        }
        return result;
    }

}

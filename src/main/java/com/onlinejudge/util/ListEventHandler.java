package com.onlinejudge.util;

import org.jetbrains.annotations.NotNull;

import static com.alibaba.fastjson.JSON.toJSONString;

public class ListEventHandler extends Handler {
    public ListEventHandler(@NotNull ListEvent e) throws InternalException{
        this.result = toJSONString(e.go());
        this.result = "{\"status\":1,\"result\":" + this.result + '}';
    }
}


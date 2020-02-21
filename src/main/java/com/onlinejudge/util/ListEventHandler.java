package com.onlinejudge.util;

import com.alibaba.fastjson.JSONArray;

public class ListEventHandler extends Handler {
    public ListEventHandler(ListEvent e) {
        this.result = JSONArray.toJSONString(e.go());
        this.result = "{\"status\":1,\"result\":" + this.result + '}';
    }
}


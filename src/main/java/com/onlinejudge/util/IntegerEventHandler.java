package com.onlinejudge.util;

public class IntegerEventHandler extends Handler {
    public IntegerEventHandler(IntegerEvent e) {
        this.result = "{\"status\":\"1\", \"result\":" + e.go() + "}";
    }
}
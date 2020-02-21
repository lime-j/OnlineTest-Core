package com.onlinejudge.util;

public class BooleanEventHandler extends Handler {
    public BooleanEventHandler(BooleanEvent e) {
        this.result = e.go() ? "{\"status\":1}" : "{\"status\":0}";
    }
}

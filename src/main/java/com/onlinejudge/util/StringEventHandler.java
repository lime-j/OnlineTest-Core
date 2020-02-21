package com.onlinejudge.util;


public class StringEventHandler extends Handler {
    public StringEventHandler(StringEvent e) {
        this.result = "{\"status\":1 , \"result\":\" " + e.go() + "\"}";
    }
}

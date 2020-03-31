package com.onlinejudge.util;


import org.jetbrains.annotations.NotNull;

public class StringEventHandler extends Handler {
    public StringEventHandler(@NotNull StringEvent e) throws InternalException {
        e.beforeGo();
        this.result = "{\"status\":1 , \"result\":\" " + e.go() + "\"}";
        e.afterGo();
    }
}

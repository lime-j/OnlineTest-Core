package com.onlinejudge.util;

import org.jetbrains.annotations.NotNull;

public class IntegerEventHandler extends Handler {
    public IntegerEventHandler(@NotNull IntegerEvent e) throws InternalException {
        this.result = "{\"status\":\"1\", \"result\":" + e.go() + "}";
    }
}
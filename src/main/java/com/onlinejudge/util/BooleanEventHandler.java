package com.onlinejudge.util;

import org.jetbrains.annotations.NotNull;

public class BooleanEventHandler extends Handler {
    public BooleanEventHandler(@NotNull BooleanEvent e) throws InternalException {
        this.result = e.go() ? "{\"status\":1}" : "{\"status\":0}";
    }
}

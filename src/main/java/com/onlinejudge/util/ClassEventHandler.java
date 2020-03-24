package com.onlinejudge.util;


import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class ClassEventHandler extends Handler {
    public ClassEventHandler(@NotNull ClassEvent e) throws InternalException {
        this.result = "{\"status\":1, " + StringUtils.strip(e.go(),"{}") + "}";
    }
}

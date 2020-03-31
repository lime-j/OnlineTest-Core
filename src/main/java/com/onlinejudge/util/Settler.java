package com.onlinejudge.util;

import org.jetbrains.annotations.Contract;

public interface Settler {
    @Contract(pure = true)
    static boolean setItem(String id) {
        return true;
    }
}

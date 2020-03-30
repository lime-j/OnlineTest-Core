package com.onlinejudge.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Provider {
    @Nullable
    @Contract(pure = true)
    static <T> List<T> getItem(String id) {
        return null;
    }
}

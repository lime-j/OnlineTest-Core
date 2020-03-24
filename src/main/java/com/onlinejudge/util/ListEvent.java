package com.onlinejudge.util;

import java.util.List;

public abstract class ListEvent<T> {
    public abstract List<T> go() throws InternalException;
}

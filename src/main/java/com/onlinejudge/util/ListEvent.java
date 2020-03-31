package com.onlinejudge.util;

import java.util.List;

public interface ListEvent<T> extends Event {
    List<T> go() throws InternalException;
}

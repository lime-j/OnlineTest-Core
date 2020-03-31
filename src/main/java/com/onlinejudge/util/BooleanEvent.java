package com.onlinejudge.util;

public interface BooleanEvent extends Event {
    boolean go() throws InternalException;
}

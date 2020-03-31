package com.onlinejudge.util;

public interface Event {
    void beforeGo() throws InternalException;

    void afterGo() throws InternalException;
}

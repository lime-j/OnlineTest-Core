package com.onlinejudge.util;

public class InternalException extends Exception {
    public InternalException(String message){
        super("InternalException: " + message);
    }
}

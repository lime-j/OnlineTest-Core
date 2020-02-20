package com.onlinejudge.daemonservice;

class TokenWrongException extends Throwable {
    TokenWrongException() {
        super("The token user send is wrong.");
    }
}

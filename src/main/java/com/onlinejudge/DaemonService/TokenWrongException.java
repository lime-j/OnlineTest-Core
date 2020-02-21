package com.onlinejudge.DaemonService;

class TokenWrongException extends Throwable {
    TokenWrongException() {
        super("The token user send is wrong.");
    }
}

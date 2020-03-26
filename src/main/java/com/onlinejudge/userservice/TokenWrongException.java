package com.onlinejudge.userservice;

import com.onlinejudge.util.InternalException;

public class TokenWrongException extends InternalException {
    public TokenWrongException() {
        super("The token user send is wrong.");
    }
}

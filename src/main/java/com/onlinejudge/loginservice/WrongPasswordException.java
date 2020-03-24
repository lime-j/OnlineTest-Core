package com.onlinejudge.loginservice;

import com.onlinejudge.util.InternalException;

public class WrongPasswordException extends InternalException {
    public WrongPasswordException(){
        super("Wrong Password!");
    }
}

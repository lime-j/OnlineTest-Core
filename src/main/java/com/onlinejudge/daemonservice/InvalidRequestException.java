package com.onlinejudge.daemonservice;

public class InvalidRequestException extends Exception {
    public InvalidRequestException(){super("Client has sent an invalid request.");}
}

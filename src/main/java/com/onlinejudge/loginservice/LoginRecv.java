package com.onlinejudge.loginservice;

public class LoginRecv {
    String userID;
    String userPassword;

    LoginRecv(String uid, String upw) {
        userID = uid;
        userPassword = upw;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}

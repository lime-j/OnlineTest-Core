package com.onlinejudge.loginservice;

import org.jetbrains.annotations.Contract;

public class LoginSend {
    public String uuid;
    public String uName;
    public int uType;
    public int uSex;


    @Contract(pure = true)
    LoginSend(String uuid, String uName, int uType, int uSex) {
        this.uuid = uuid;
        this.uName = uName;
        this.uType = uType;
        this.uSex = uSex;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getuType() {
        return uType;
    }

    public void setuType(int uType) {
        this.uType = uType;
    }
    public int getuSex() {
        return uSex;
    }
    public void setuSex(int uSex) {
        this.uSex = uSex;
    }
}

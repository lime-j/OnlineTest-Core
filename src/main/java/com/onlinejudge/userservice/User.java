package com.onlinejudge.userservice;

public class User {
    private String userID;
    private String userName;
    private int userSex;
    private int userType;
    @SuppressWarnings("FieldCanBeLocal")
    private String tuid;

    public User(String userID, String userName, int userSex, int userType) {
        this.userID = userID;
        this.userName = userName;
        this.userSex = userSex;
        this.userType = userType;
    }

    public User(String userID, String userName, int userSex, int userType, String tuid) {
        this.userID = userID;
        this.userName = userName;
        this.userSex = userSex;
        this.userType = userType;
        this.tuid = tuid;
    }

    public int getUserType() {
        return this.userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserSex() {
        return this.userSex;
    }

    public void setUserSex(int userSex) {
        this.userSex = userSex;
    }

    public String getUserID() {
        return this.userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}

package com.onlinejudge.manservice;

import com.onlinejudge.util.StringEvent;

public class ManServiceGetPerfStatus implements StringEvent {
    private String userID, userToken;

    public ManServiceGetPerfStatus(String userID, String userToken) {
        this.userID = userID;
        this.userToken = userToken;
    }

    @Override
    public void beforeGo() {
        // do nothing
    }

    @Override
    public void afterGo() {
        // do nothing
    }

    public String go() {
        return "http://106.54.221.110/tetz2.php?user=" + this.userID + "&token=" + this.userToken;
    }

    public String getUserToken() {
        return this.userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getUserID() {
        return this.userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
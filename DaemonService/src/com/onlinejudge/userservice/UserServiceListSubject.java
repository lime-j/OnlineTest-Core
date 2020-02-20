package com.onlinejudge.userservice;

import com.onlinejudge.util.ListEvent;
import com.onlinejudge.util.StringListerUtil;

import java.util.ArrayList;
import java.util.List;

public class UserServiceListSubject extends ListEvent {
    private String userID;

    public UserServiceListSubject(String userID) {
        this.userID = userID;
    }

    public List<String> go() {
        List<String> resultList = new ArrayList<>();
        try {
            return (new StringListerUtil(
                    "select * from teasubject where teacherid = ? ", "subject", this.userID, this.toString()
            )).getResultList();
        } catch (Exception e) {
            return new ArrayList<String>();
        }
    }

    public String getUserID() {
        return this.userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}

package com.onlinejudge.manservice;

import com.alibaba.fastjson.JSONObject;
import com.onlinejudge.util.BooleanEvent;

import java.util.List;

public class ManServiceAddListUsers implements BooleanEvent {
    private final List<String> currUserList;

    public ManServiceAddListUsers(List<String> OrigionUserData) {
        this.currUserList = OrigionUserData;
    }

    @Override
    public void beforeGo() {
        // do nothing
    }

    @Override
    public void afterGo() {
        // do nothing
    }

    @Override
    public boolean go() {
        for (String s : this.currUserList) {
            JSONObject CurrJSON = JSONObject.parseObject(s);
            UserWithPasswd CurrUser = new UserWithPasswd(
                    CurrJSON.getString("uid"), CurrJSON.getString("uname"),
                    CurrJSON.getIntValue("usex"), CurrJSON.getIntValue("utype"),
                    CurrJSON.getString("passwd"));
            CurrUser.updateUser();
        }
        return true;
    }

    public List<String> getUserlist() {
        return this.currUserList;
    }
}

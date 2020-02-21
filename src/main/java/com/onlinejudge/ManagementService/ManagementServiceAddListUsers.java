package com.onlinejudge.ManagementService;

import com.alibaba.fastjson.JSONObject;
import com.onlinejudge.util.BooleanEvent;

import java.util.List;

public class ManagementServiceAddListUsers extends BooleanEvent {
    private List<String> currUserList;

    public ManagementServiceAddListUsers(List<String> OrigionUserData) {
        this.currUserList = OrigionUserData;
    }

    @Override
    public boolean go() {
        for (String s : this.currUserList) {
            JSONObject CurrJSON = JSONObject.parseObject(s);
            UserWithPasswd CurrUser = new UserWithPasswd(
                    CurrJSON.getString("uid"), CurrJSON.getString("uname"),
                    CurrJSON.getIntValue("usex"), CurrJSON.getIntValue("utype"),
                    CurrJSON.getString("teacher"), CurrJSON.getString("passwd"));
            CurrUser.updateUser();
            //            if (!CurrUser.updateUser()) {
////                return false;
////            }
        }
        return true;
    }

    public List<String> getUserlist() {
        return this.currUserList;
    }
}

package com.onlinejudge.userservice;

import lombok.Getter;

@Getter
public enum UserServiceProperties {
    changeUserSex(1), changeUserName(2), changeUserPassword(3);
    int id;

    UserServiceProperties(int t) {
        this.id = t;
    }
}

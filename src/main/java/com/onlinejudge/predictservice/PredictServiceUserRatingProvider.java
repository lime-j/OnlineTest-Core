package com.onlinejudge.predictservice;

import com.onlinejudge.util.Provider;

import java.util.ArrayList;
import java.util.List;

public class PredictServiceUserRatingProvider implements Provider {
    public static List<UserStar>  getItem(String examID){
        return new ArrayList<>();
    }
}

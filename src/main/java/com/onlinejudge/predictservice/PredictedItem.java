package com.onlinejudge.predictservice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PredictedItem {
    private String examID;
    private double value;
}

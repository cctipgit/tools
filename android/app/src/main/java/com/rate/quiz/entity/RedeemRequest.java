package com.rate.quiz.entity;

public class RedeemRequest {
    String id;

    public static RedeemRequest of(String id) {
        RedeemRequest r = new RedeemRequest();
        r.id = id;
        return r;
    }
}

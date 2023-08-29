package com.rate.quiz.entity;

public class RedeemHistoryItem {

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAIL = 2;

    public int status;
    public long time;
    public String title;
}

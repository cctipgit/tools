package com.rate.quiz.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RedeemPointList {
    public List<RedeemPointItem> list;
    @SerializedName("total_num")
    public int totalNum;
    @SerializedName("total_points")
    public long totalPoints;
}

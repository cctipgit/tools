package com.hash.coinconvert.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RedeemPointList {
    public List<RedeemItem> list;
    @SerializedName("total_num")
    public int totalNum;
    @SerializedName("total_points")
    public long totalPoints;
}

package com.hash.coinconvert.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RedeemHistoryList {
    public List<RedeemHistoryItem> list;
    @SerializedName("total_num")
    public int totalNum;
}

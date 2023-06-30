package com.hash.coinconvert.entity;

import com.google.gson.annotations.SerializedName;

public class RedeemHistoryItem {
    @SerializedName("point_change")
    public String pointChange;
    public long time;
    public String title;
}

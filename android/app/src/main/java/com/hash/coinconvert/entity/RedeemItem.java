package com.hash.coinconvert.entity;

import com.google.gson.annotations.SerializedName;

public class RedeemItem {
    public String id;
    public String pic;
    @SerializedName("point_require")
    public long pointRequire;
    @SerializedName("redeem_num")
    public long redeemNum;
    public String reward;
    @SerializedName("reward_desc")
    public String rewardDesc;
    public long total;
}

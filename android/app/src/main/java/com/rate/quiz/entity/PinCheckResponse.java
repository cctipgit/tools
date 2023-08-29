package com.rate.quiz.entity;

import com.google.gson.annotations.SerializedName;

public class PinCheckResponse {
    @SerializedName("pin_id")
    public String id;
    @SerializedName("pin_pic")
    public String pic;
    @SerializedName("pin_reward")
    public String reward;
    @SerializedName("pin_reward_desc")
    public String rewardDesc;
}

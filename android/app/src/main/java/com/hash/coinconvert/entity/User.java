package com.hash.coinconvert.entity;

import com.google.gson.annotations.SerializedName;

import java.text.NumberFormat;

public class User {
    public String avatar;
    @SerializedName("pin_chance")
    public int pinChance;
    public int point;
    public String uid;
    @SerializedName("user_name")
    public String userName;
    public long created;

    public String getFormattedPoint(){
        return NumberFormat.getNumberInstance().format(point);
    }
}

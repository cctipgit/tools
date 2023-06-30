package com.hash.coinconvert.entity;

import com.google.gson.annotations.SerializedName;

public class ServerUserInfo {
    public String avatar;
    @SerializedName("pin_change")
    public int pinChange;
    public int point;
    public String uid;
    @SerializedName("user_name")
    public String userName;
}

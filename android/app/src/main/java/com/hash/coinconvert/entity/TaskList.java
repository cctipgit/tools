package com.hash.coinconvert.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TaskList {
    public long expireTime;
    @SerializedName("lists")
    public List<TaskItem> list;
    @SerializedName("pin_num")
    public int pinNum;
}

package com.rate.quiz.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TaskList {
    @SerializedName("expire_time")
    public long expireTime;
    @SerializedName("lists")
    public List<TaskItem> list;
    @SerializedName("pin_num")
    public int pinNum;
}

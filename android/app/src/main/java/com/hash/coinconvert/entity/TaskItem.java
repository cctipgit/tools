package com.hash.coinconvert.entity;

import com.google.gson.annotations.SerializedName;

public class TaskItem {
    public boolean done;
    public String params;
    @SerializedName("spin_times")
    public int spinTimes;
    @SerializedName("task_id")
    public String id;
    @SerializedName("task_name")
    public String name;
    @SerializedName("task_type")
    public String type;
}

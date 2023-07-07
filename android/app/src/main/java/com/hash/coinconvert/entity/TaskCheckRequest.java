package com.hash.coinconvert.entity;

import com.google.gson.annotations.SerializedName;

public class TaskCheckRequest {
    public String param;
    @SerializedName("task_id")
    public String taskId;

    public TaskCheckRequest(String param, String taskId) {
        this.param = param;
        this.taskId = taskId;
    }
}

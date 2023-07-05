package com.hash.coinconvert.utils.task;

import android.content.Context;

import com.hash.coinconvert.entity.TaskItem;

import java.util.Map;

public class NoActionTaskHandle implements TaskHandler{
    @Override
    public boolean invoke(Context context, TaskItem taskItem, Map<String, String> params) {
        return false;
    }
}
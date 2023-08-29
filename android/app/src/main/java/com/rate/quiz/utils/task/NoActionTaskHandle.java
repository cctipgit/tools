package com.rate.quiz.utils.task;

import android.content.Context;

import com.rate.quiz.entity.TaskItem;

import java.util.Map;

public class NoActionTaskHandle extends AbstractTaskHandler{
    @Override
    public boolean invoke(Context context, TaskItem taskItem, Map<String, String> params) {
        return true;
    }
}

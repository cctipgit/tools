package com.hash.coinconvert.utils.task;

import android.content.Context;

import com.hash.coinconvert.entity.TaskItem;

import java.util.Map;

public interface TaskHandler {
    /**
     *
     * @return if true,means this is a async task
     */
    boolean invoke(Context context, TaskItem taskItem, Map<String,String> params);
}

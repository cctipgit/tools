package com.rate.quiz.utils.task;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.rate.quiz.entity.TaskItem;

import java.util.Map;

public class VisitWebsiteTaskHandler extends AbstractTaskHandler {
    @Override
    public boolean invoke(Context context, TaskItem taskItem, Map<String, String> params) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(taskItem.params));
        context.startActivity(intent);
        return true;
    }
}

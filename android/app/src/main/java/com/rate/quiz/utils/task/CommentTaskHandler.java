package com.rate.quiz.utils.task;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.rate.quiz.entity.TaskItem;

import java.util.Map;

public class CommentTaskHandler extends AbstractTaskHandler {
    @Override
    public boolean invoke(Context context, TaskItem taskItem, Map<String, String> params) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return true;
    }
}

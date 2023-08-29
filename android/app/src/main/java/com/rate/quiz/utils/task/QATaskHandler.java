package com.rate.quiz.utils.task;

import android.content.Context;
import android.os.Bundle;

import androidx.navigation.NavController;

import com.rate.quiz.R;
import com.rate.quiz.entity.TaskItem;

import java.util.Map;

public class QATaskHandler extends AbstractTaskHandler {
    @Override
    public boolean invoke(Context context, TaskItem taskItem, Map<String, String> params) {
        return TaskHelper.isQAChecked();
    }

    @Override
    public void dispatch(NavController navController, Bundle bundle) {
        switchTab(navController.getContext(),R.id.qa);
    }
}

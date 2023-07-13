package com.hash.coinconvert.utils.task;

import android.content.Context;
import android.os.Bundle;

import androidx.navigation.NavController;

import com.hash.coinconvert.R;
import com.hash.coinconvert.entity.TaskItem;

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

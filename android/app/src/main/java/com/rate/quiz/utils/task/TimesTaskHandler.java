package com.rate.quiz.utils.task;

import android.content.Context;
import android.os.Bundle;

import androidx.navigation.NavController;

import com.rate.quiz.R;
import com.rate.quiz.entity.TaskItem;

import java.util.Map;

public class TimesTaskHandler extends AbstractTaskHandler {

    private String spKey;

    protected String getKey() {
        return spKey;
    }

    public TimesTaskHandler(String key) {
        this.spKey = key;
    }

    @Override
    public boolean invoke(Context context, TaskItem taskItem, Map<String, String> params) {
        return TaskHelper.isTaskFinished(spKey, taskItem.getRequiredTimes());
    }

    @Override
    public void dispatch(NavController navController, Bundle bundle) {
        switch (spKey) {
            case KEY_TASK_ADD_TOKEN:
            case KEY_TASK_VIEW_TOKEN_DETAIL:
                switchTab(navController.getContext(), R.id.currency);
                break;
            case KEY_PIN_AT_10_12:
            case KEY_PIN_AT_14_16:
                navController.navigate(R.id.fragment_lottery, bundle);
                break;
            case KEY_REDEEM:
                switchTab(navController.getContext(), R.id.redeem);
                break;
        }
    }
}

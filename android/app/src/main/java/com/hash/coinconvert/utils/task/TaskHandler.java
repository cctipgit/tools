package com.hash.coinconvert.utils.task;

import android.content.Context;
import android.os.Bundle;

import androidx.navigation.NavController;

import com.hash.coinconvert.entity.TaskItem;

import java.util.Map;

public interface TaskHandler {

    String KEY_TASK_INVITE = "task_invite";
    String KEY_TASK_ADD_TOKEN = "task_add_token";
    String KEY_TASK_VIEW_TOKEN_DETAIL = "task_token_detail";
    /**
     * save the timestamp of qa check time
     */
    String KEY_QUESTIONNAIRE = "task_qa";
    String KEY_REDEEM = "task_redeem";
    String KEY_PIN_AT_10_12 = "task_pin_1012";
    String KEY_PIN_AT_14_16 = "task_pin_1416";

    /**
     * @return true if the task is finished
     */
    boolean invoke(Context context, TaskItem taskItem, Map<String, String> params);

    void dispatch(NavController navController, Bundle bundle);
}

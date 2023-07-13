package com.hash.coinconvert.utils.task;

import android.text.format.DateUtils;

import com.duxl.baselib.utils.SPUtils;

import java.util.Calendar;
import java.util.Date;

public class TaskHelper {
    public static void viewTokenDetail() {
        increase(TaskHandler.KEY_TASK_VIEW_TOKEN_DETAIL);
    }

    public static boolean isViewTokenDetailFinished(int requiredTimes) {
        return isTaskFinished(TaskHandler.KEY_TASK_VIEW_TOKEN_DETAIL, requiredTimes);
    }

    public static boolean isAddTokenFinished(int requiredTimes) {
        return isTaskFinished(TaskHandler.KEY_TASK_ADD_TOKEN, requiredTimes);
    }

    public static boolean isInviteFinished(int requiredTimes) {
        return isTaskFinished(TaskHandler.KEY_TASK_INVITE, requiredTimes);
    }

    public static void addToken() {
        increase(TaskHandler.KEY_TASK_ADD_TOKEN);
    }

    public static boolean isTaskFinished(String key, int requiredTimes) {
        return SPUtils.getInstance().getInt(key) >= requiredTimes;
    }

    public static void invite() {
        increase(TaskHandler.KEY_TASK_INVITE);
    }

    public static void reset(String key) {
        SPUtils.getInstance().put(key, 0);
    }

    public static void qaCheck() {
        SPUtils.getInstance().put(TaskHandler.KEY_QUESTIONNAIRE, System.currentTimeMillis());
    }

    public static boolean isQAChecked() {
        long time = SPUtils.getInstance().getInt(TaskHandler.KEY_QUESTIONNAIRE);
        return DateUtils.isToday(time);
    }

    public static void redeem() {
        increase(TaskHandler.KEY_REDEEM);
    }

    public static void pin() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (10 <= hour && hour < 12) {
            increase(TaskHandler.KEY_PIN_AT_10_12);
        } else if (14 <= hour && hour < 16) {
            increase(TaskHandler.KEY_PIN_AT_14_16);
        }
    }

    private static void increase(String key) {
        int count = SPUtils.getInstance().getInt(key, 0);
        SPUtils.getInstance().put(key, count + 1);
    }
}

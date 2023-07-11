package com.hash.coinconvert.utils.task;

import android.content.Context;
import android.os.Bundle;

import androidx.navigation.NavController;

import com.hash.coinconvert.entity.TaskItem;

import java.util.Map;

public class TaskHandlerFactory implements TaskHandler {

    private TaskHandler proxy;


    @Override
    public boolean invoke(Context context, TaskItem taskItem, Map<String, String> params) {
        proxy = findHandler(context, taskItem, params);
        return proxy.invoke(context, taskItem, params);
    }

    @Override
    public void dispatch(NavController navController, Bundle bundle) {
        proxy.dispatch(navController, bundle);
    }

    public TaskHandler getProxy() {
        return proxy;
    }

    public boolean isLottery() {
        String key = null;
        if (proxy instanceof TimesTaskHandler) {
            key = ((TimesTaskHandler) proxy).getKey();
        }
        return TaskHandler.KEY_PIN_AT_10_12.equals(key) || TaskHandler.KEY_PIN_AT_14_16.equals(key);
    }

    private TaskHandler findHandler(Context context, TaskItem taskItem, Map<String, String> params) {
        TaskHandler proxy;
        String type = taskItem.type;
        switch (type) {
            case "share_to_discord":
            case "share_to_telegram":
            case "share_to_twitter":
            case "share_to_facebook":
                proxy = new ShareTaskHandler();
                break;
            case "share_to_1_register":
            case "share_to_3_register":
            case "share_to_5_register":
                TaskHelper.invite();
                proxy = new InviteTaskHandler();
                break;
            case "share_add_token_1":
            case "share_add_token_3":
                proxy = new TimesTaskHandler(TaskHandler.KEY_TASK_ADD_TOKEN);
                break;
            case "view_token_1":
            case "view_token_3":
                proxy = new TimesTaskHandler(TaskHandler.KEY_TASK_VIEW_TOKEN_DETAIL);
                break;
            case "quiz_done":
                proxy = new QATaskHandler();
                break;
            case "visit_website":
                proxy = new VisitWebsiteTaskHandler();
                break;
            case "product_exchange":
                proxy = new TimesTaskHandler(TaskHandler.KEY_REDEEM);
                break;
            case "product_get_during_10_12":
                proxy = new TimesTaskHandler(TaskHandler.KEY_PIN_AT_10_12);
                break;
            case "product_get_during_14_16":
                proxy = new TimesTaskHandler(TaskHandler.KEY_PIN_AT_14_16);
                break;
            case "product_get_app_star":
                proxy = new CommentTaskHandler();
                break;
            case "sign in":
            default:
                proxy = new NoActionTaskHandle();
        }
        return proxy;
    }

    @Deprecated
    public static boolean execute(Context context, TaskItem taskItem, Map<String, String> params) {
        TaskHandler proxy;
        String type = taskItem.type;
        switch (type) {
            case "share_to_discord":
            case "share_to_telegram":
            case "share_to_twitter":
            case "share_to_facebook":
                proxy = new ShareTaskHandler();
                break;
            case "share_to_1_register":
            case "share_to_3_register":
            case "share_to_5_register":
                TaskHelper.invite();
                proxy = new InviteTaskHandler();
                break;
            case "share_add_token_1":
            case "share_add_token_3":
                proxy = new TimesTaskHandler(TaskHandler.KEY_TASK_ADD_TOKEN);
                break;
            case "view_token_1":
            case "view_token_3":
                proxy = new TimesTaskHandler(TaskHandler.KEY_TASK_VIEW_TOKEN_DETAIL);
                break;
            case "quiz_done":
                proxy = new QATaskHandler();
                break;
            case "visit_website":
                proxy = new VisitWebsiteTaskHandler();
                break;
            case "product_exchange":
                proxy = new TimesTaskHandler(TaskHandler.KEY_REDEEM);
                break;
            case "product_get_during_10_12":
                proxy = new TimesTaskHandler(TaskHandler.KEY_PIN_AT_10_12);
                break;
            case "product_get_during_14_16":
                proxy = new TimesTaskHandler(TaskHandler.KEY_PIN_AT_14_16);
                break;
            case "product_get_app_star":
                proxy = new CommentTaskHandler();
                break;
            case "sign in":
            default:
                proxy = new NoActionTaskHandle();
        }
        return proxy.invoke(context, taskItem, params);
    }
}

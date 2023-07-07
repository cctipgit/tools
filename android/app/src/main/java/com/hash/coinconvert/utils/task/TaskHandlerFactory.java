package com.hash.coinconvert.utils.task;

import android.content.Context;

import com.hash.coinconvert.entity.TaskItem;

import java.util.Map;

public class TaskHandlerFactory{

    public static boolean execute(Context context,TaskItem taskItem,Map<String,String> params){
        TaskHandler proxy;
        String type = taskItem.type;
        if (type.startsWith("share")) {
            proxy = new ShareTaskHandler();
        } else if ("visit_website".equals(type)) {
            proxy = new VisitWebsiteTaskHandler();
        }else {
            proxy = null;
        }
        if (proxy != null) return proxy.invoke(context, taskItem, params);
        return false;
    }
}

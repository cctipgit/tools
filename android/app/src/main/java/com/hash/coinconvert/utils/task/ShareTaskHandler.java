package com.hash.coinconvert.utils.task;

import android.content.Context;

import com.hash.coinconvert.entity.TaskItem;
import com.hash.coinconvert.utils.ShareHelper;

import java.util.Map;

public class ShareTaskHandler implements TaskHandler{
    @Override
    public boolean invoke(Context context, TaskItem taskItem, Map<String, String> params) {
        //TODO
        ShareHelper.shareText(context, taskItem.name);
        return true;
    }
}

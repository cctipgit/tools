package com.hash.coinconvert.utils.task;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.hash.coinconvert.entity.TaskItem;
import com.hash.coinconvert.utils.GsonHelper;
import com.hash.coinconvert.utils.ShareHelper;

import java.util.Map;

public class InviteTaskHandler extends AbstractTaskHandler {
    @Override
    public boolean invoke(Context context, TaskItem taskItem, Map<String, String> params) {
        if (!TextUtils.isEmpty(taskItem.params)) {
            try {
                Share share = GsonHelper.fromJsonString(taskItem.params, Share.class);
                ShareHelper.shareText(context, share.url);
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        }
        return TaskHelper.isInviteFinished(taskItem.getRequiredTimes());
    }

    private static class Share {
        @SerializedName("share_url")
        String url;
    }
}

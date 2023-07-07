package com.hash.coinconvert.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

import com.duxl.baselib.utils.SPUtils;

import java.util.UUID;

public class UUIDUtils {
    public static final String KEY = "UID";

    private static String genUUID(Context context) {
        @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (TextUtils.isEmpty(deviceId)) {
            return UUID.randomUUID().toString();
        } else {
            return UUID.nameUUIDFromBytes(deviceId.getBytes()).toString();
        }
    }

    public static void init(Context context) {
        String id = genUUID(context);
        SPUtils.getInstance().put(KEY, id);
    }

    public static String getUUID() {
        String id = SPUtils.getInstance().getString(KEY);
        if (TextUtils.isEmpty(id)) {
            throw new IllegalStateException("call UUIDUtils.init first");
        }
        return id;
    }
}

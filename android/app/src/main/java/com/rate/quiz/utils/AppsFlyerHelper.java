package com.rate.quiz.utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.attribution.AppsFlyerRequestListener;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.rate.quiz.livedatabus.event.AppsFlyerEvent;

import java.util.Map;

public final class AppsFlyerHelper {
    public static final String AF_DEV_KEY = "gUyT294NkvpnTkQBYSDLXC";
    private static final String TAG = "AppsFlyerHelper";

    private AppsFlyerHelper() {
    }

    public static void init(Application application) {
        AppsFlyerLib.getInstance().setDebugLog(true);
        AppsFlyerLib.getInstance().init(AF_DEV_KEY, new AppsFlyerConversionListener() {
            @Override
            public void onConversionDataSuccess(Map<String, Object> map) {
                Log.d(TAG, "onConversionDataSuccess:");
                if (map != null) {
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        Log.d(TAG, "onConversionDataSuccess:key:" + entry.getKey() + ",value:" + entry.getValue());
                    }
                }
                LiveEventBus.get(AppsFlyerEvent.KEY).post(AppsFlyerEvent.success(map));
            }

            @Override
            public void onConversionDataFail(String s) {
                Log.d(TAG, "onConversionDataFail");
                LiveEventBus.get(AppsFlyerEvent.KEY).post(AppsFlyerEvent.error(s));
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> map) {
                Log.d(TAG, "onAppOpenAttribution");
                if (map != null) {
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        Log.d(TAG, "onConversionDataSuccess:key:" + entry.getKey() + ",value:" + entry.getValue());
                    }
                }
            }

            @Override
            public void onAttributionFailure(String s) {
                Log.d(TAG, "onAttributionFailure");
            }
        }, application);

        AppsFlyerLib.getInstance().start(application, AF_DEV_KEY, new AppsFlyerRequestListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess");
            }

            @Override
            public void onError(int i, @NonNull String s) {
                Log.e(TAG, "onError:" + i + "," + s);
            }
        });
    }

    public static void logEvent(Context context,
                                String eventName,
                                Map<String, Object> eventValues) {
        AppsFlyerLib.getInstance().logEvent(context, eventName, eventValues, new AppsFlyerRequestListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "logEvent onSuccess");
            }

            @Override
            public void onError(int i, @NonNull String s) {
                Log.e(TAG, "log event onError:" + s);
            }
        });
    }
}

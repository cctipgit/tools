package com.rate.quiz.rnmodule;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.rate.quiz.App;
import com.rate.quiz.ui2.activity.HomeActivity;
import com.rate.quiz.utils.AppsFlyerHelper;
import com.rate.quiz.utils.GsonHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * for open native page in react native
 * usage:
 * import {NativeModule} from 'react-native'
 * NativeModule.ToolModule.openNative()
 */
public class ToolModule extends ReactContextBaseJavaModule {

    public static final String NAME = "ToolModule";

    private ReactApplicationContext context;

    public ToolModule(ReactApplicationContext context) {
        this.context = context;
    }

    @NonNull
    @Override
    public String getName() {
        return NAME;
    }

    @ReactMethod
    public void openNative() {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        Objects.requireNonNull(context.getCurrentActivity()).finish();
    }

    @ReactMethod
    public void getAppsFlyerConversionData(Promise promise) {
        App app = ((App) context.getApplicationContext());
        Map<String, Object> params = null;
        List<ReactPackage> packageList = app.getReactNativeHost().getReactInstanceManager().getPackages();
        for (ReactPackage reactPackage : packageList) {
            if (reactPackage instanceof ToolModulePackage) {
                params = ((ToolModulePackage) reactPackage).appsFlyerConversation;
            }
        }
        Log.d(NAME, "getParams" + GsonHelper.getGsonInstance().toJson(params));
        promise.resolve(GsonHelper.getGsonInstance().toJson(params));
    }

    @ReactMethod
    public void logEvent(ReadableMap json, Promise promise) {
        try {
            Map<String, Object> map = json.toHashMap();
            Object eventName = map.get("event");
            if (eventName == null) {
                promise.reject("missing \"event\" field");
                return;
            }
            Object params = map.get("params");
            Map<String, Object> paramMap = null;
            if (params instanceof Map) {
                paramMap = ((Map<String, Object>) params);
            } else if (params instanceof String) {
                paramMap = GsonHelper.fromJsonString(((String) params), HashMap.class);
            }
            AppsFlyerHelper.logEvent(context, eventName.toString(), paramMap);
            promise.resolve(null);
        } catch (Exception e) {
            e.printStackTrace();
            promise.reject(e);
        }
    }
}

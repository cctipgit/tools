package com.hash.coinconvert.rnmodule;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.hash.coinconvert.ui2.activity.HomeActivity;

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
}

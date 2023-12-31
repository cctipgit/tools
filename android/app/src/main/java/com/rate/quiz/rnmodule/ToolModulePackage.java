package com.rate.quiz.rnmodule;

import androidx.annotation.NonNull;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.rate.quiz.rnmodule.view.WebViewViewManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ToolModulePackage implements ReactPackage {
    public Map<String, Object> appsFlyerConversation;

    @NonNull
    @Override
    public List<NativeModule> createNativeModules(@NonNull ReactApplicationContext reactApplicationContext) {
        return Collections.singletonList(new ToolModule(reactApplicationContext));
    }

    @NonNull
    @Override
    public List<ViewManager> createViewManagers(@NonNull ReactApplicationContext reactApplicationContext) {
        List<ViewManager> managers = new ArrayList<>();
//        managers.add(new WebViewViewManager(reactApplicationContext));
        managers.add(new WebViewViewManager(reactApplicationContext));
        return managers;
    }
}

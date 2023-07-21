package com.hash.coinconvert.rnmodule.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.Map;

public class WebViewViewManager2 extends SimpleViewManager<MultiWebView> {
    public static final String NAME = "CCTipWebView";


    private ReactApplicationContext context;

    public WebViewViewManager2(ReactApplicationContext context) {
        this.context = context;
    }

    @NonNull
    @Override
    public String getName() {
        return NAME;
    }

    @NonNull
    @Override
    protected MultiWebView createViewInstance(@NonNull ThemedReactContext themedReactContext) {
        return new MultiWebView(themedReactContext);
    }

    @ReactProp(name = "url")
    public void setUrl(MultiWebView view, String url) {
        view.setUrl(url);
    }

    @Nullable
    @Override
    public Map getExportedCustomBubblingEventTypeConstants() {
        return MapBuilder.builder()
                .put("onMessage",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onMessage")
                        )
                )
                .build();
    }
}

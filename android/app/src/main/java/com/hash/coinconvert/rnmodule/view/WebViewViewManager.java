package com.hash.coinconvert.rnmodule.view;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Choreographer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.duxl.baselib.utils.DisplayUtil;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.Map;

public class WebViewViewManager extends SimpleViewManager<FrameLayout> {
    public static final String NAME = "CCTipWebView";

    public static final int COMMAND_CREATE = 1;

    private ReactApplicationContext context;
    private int reactNativeViewId;
    private String url;
    private int width;
    private int height;

    public WebViewViewManager(ReactApplicationContext context) {
        this.context = context;
    }

    @NonNull
    @Override
    public String getName() {
        return NAME;
    }

    @NonNull
    @Override
    protected FrameLayout createViewInstance(@NonNull ThemedReactContext themedReactContext) {
        FrameLayout frameLayout = new FrameLayout(themedReactContext);
        ViewGroup.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        frameLayout.setLayoutParams(params);
        frameLayout.setBackgroundColor(Color.RED);
        return frameLayout;
    }

    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of("create", COMMAND_CREATE);
    }

    @Override
    public void receiveCommand(@NonNull FrameLayout root, String commandId, @Nullable ReadableArray args) {
        super.receiveCommand(root, commandId, args);
        reactNativeViewId = args.getInt(0);
        int commandIdInt = Integer.parseInt(commandId);
        Log.d(NAME, "commandIdInt:" + commandIdInt + "," + reactNativeViewId);
        switch (commandIdInt) {
            case COMMAND_CREATE:
                createFragment(root, reactNativeViewId);
                break;
            default:
        }
    }

    public void createFragment(FrameLayout root, int reactNativeViewId) {
        ViewGroup parentView = (ViewGroup) root.findViewById(reactNativeViewId).getParent();
        parentView.setBackgroundColor(Color.BLUE);
        Log.d(NAME, "frame:" + root.getId());
        Log.d(NAME, "parentView:" + parentView);
        setupLayout(parentView);
        final WebFragment myFragment = new WebFragment();
        Bundle bundle = new Bundle();
        Log.d(NAME, "url:" + url);
        bundle.putString("url", url);
        myFragment.setArguments(bundle);
        FragmentActivity activity = (FragmentActivity) context.getCurrentActivity();
        activity.getSupportFragmentManager().beginTransaction().replace(reactNativeViewId, myFragment, String.valueOf(reactNativeViewId)).commit();
    }

    public void setupLayout(View view) {
        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                manuallyLayoutChildren(view);
                view.getViewTreeObserver().dispatchOnGlobalLayout();
                Choreographer.getInstance().postFrameCallback(this);
            }
        });
    }

    public void manuallyLayoutChildren(View view) {
        // propWidth and propHeight coming from react-native props
        this.width = DisplayUtil.getScreenWidth(context);
        this.height = DisplayUtil.getScreenHeight(context);
        view.measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
        view.layout(0, 0, width, height);
    }

    @ReactProp(name = "url")
    public void setUrl(FrameLayout view, String url) {
        this.url = url;
    }

    @ReactProp(name = "width", defaultInt = ViewGroup.LayoutParams.MATCH_PARENT)
    public void setWidth(FrameLayout view, int width) {
        this.width = width;
    }

    @ReactProp(name = "height", defaultInt = ViewGroup.LayoutParams.MATCH_PARENT)
    public void setHeight(FrameLayout view, int height) {
        this.height = height;
    }
}

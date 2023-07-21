package com.hash.coinconvert.rnmodule.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.hash.coinconvert.R;

public class MultiWebView extends LinearLayout implements ClientProxy, Evaluate {

    private Toolbar toolbar;
    private WebView webView;
    private FrameLayout flContainer;

    public MultiWebView(@NonNull Context context) {
        super(context);
        init();
    }

    public MultiWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MultiWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        View.inflate(getContext(), R.layout.view_rn_web, this);
        flContainer = findViewById(R.id.fl_container);
        toolbar = findViewById(R.id.toolbar);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        flContainer.removeAllViews();
    }

    public void setUrl(String url) {
        Log.d(WebViewViewManager.NAME, "setUrl:" + url);
        ensureWebView();
        webView.loadUrl(url);
    }

    private void ensureWebView() {
        if (webView == null || webView.getParent() == null) {
            webView = new WebView(getContext());
            initWebViewSetting(webView, this, this);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            webView.setLayoutParams(params);
            flContainer.addView(webView);
        }
    }

    @Override
    public void onTitle(String title) {
        toolbar.setTitle(title);
    }

    @Override
    public void onProgress(int progress) {

    }

    public static void initWebViewSetting(WebView webView, ClientProxy proxy, Evaluate evaluate) {
        WebSettings settings = webView.getSettings();
        CookieManager manager = CookieManager.getInstance();
        manager.setAcceptCookie(true);
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setUserAgentString(settings.getUserAgentString().replace("; wv", ""));
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(true);
        webView.addJavascriptInterface(new JsBridge(evaluate), JsBridge.NAME);
        webView.setWebChromeClient(new CustomWebChromeClient(proxy));
        webView.setWebViewClient(new CustomWebViewClient(proxy));
    }

    @Override
    public void evaluateJs(String message) {
        WritableMap event = Arguments.createMap();
        event.putString("message", message);
        ReactContext context = (ReactContext) getContext();
        context.getJSModule((RCTEventEmitter.class)).receiveEvent(getId(), "onMessage", event);
    }
}

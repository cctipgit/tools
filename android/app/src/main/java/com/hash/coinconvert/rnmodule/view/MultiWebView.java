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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.hash.coinconvert.R;
import com.hash.coinconvert.utils.StatusBarUtils;

public class MultiWebView extends LinearLayout implements ClientProxy, Evaluate {

    public static final String TAG = "MultiWebView";

    private ProgressBar progressBar;
    private WebView webView;
    private FrameLayout flContainer;
    private ImageButton btnBack;

    private boolean showNavigation;

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
        setTag(TAG);
        setOrientation(VERTICAL);
        View.inflate(getContext(), R.layout.view_rn_web, this);
        flContainer = findViewById(R.id.fl_container);
        progressBar = findViewById(R.id.progress_bar);
        btnBack = findViewById(R.id.img_back);
        btnBack.setOnClickListener(v -> {
            handleOnBackPressed();
        });
        btnBack.setVisibility(View.GONE);
        View v = ((ViewGroup) flContainer.getParent());
        v.setPadding(0, StatusBarUtils.getStatusBarHeight(getContext()), 0, 0);
    }

    public void handleOnBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        flContainer.removeAllViews();
    }

    public void setUrl(String url) {
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

    public boolean goBack() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }

    @Override
    public void onTitle(String title) {
    }

    @Override
    public void onProgress(int progress) {
        progressBar.setProgress(progress);
    }

    @Override
    public void onCanGoBack(boolean canGoBack) {
        if (!showNavigation) return;
        Log.d("CCTipWebView", "onCan:" + canGoBack);
        post(() -> {
            btnBack.setVisibility(canGoBack ? View.VISIBLE : View.GONE);

            Log.d("CCTipWebView", "onCannnnn:" + (btnBack.getVisibility()));
        });
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
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.addJavascriptInterface(new JsBridge(evaluate), JsBridge.NAME);
        if (proxy != null) {
            webView.setWebChromeClient(new CustomWebChromeClient(proxy));
            webView.setWebViewClient(new CustomWebViewClient(proxy));
        }
    }

    @Override
    public void evaluateJs(String key,String message) {
        WritableMap event = Arguments.createMap();
        event.putString("message", message);
        event.putString("key",key);
        ReactContext context = (ReactContext) getContext();
        context.getJSModule((RCTEventEmitter.class)).receiveEvent(getId(), "onMessage", event);
    }

    public void setShowNavigation(boolean show) {
        this.showNavigation = show;
    }
}

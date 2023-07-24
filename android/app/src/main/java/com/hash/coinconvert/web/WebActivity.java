package com.hash.coinconvert.web;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.hash.coinconvert.R;
import com.hash.coinconvert.databinding.ActivityWebBinding;
import com.hash.coinconvert.ui2.activity.BaseActivity;

public class WebActivity extends BaseActivity {
    private ActivityWebBinding binding;
    private WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWebBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.addMenu(R.drawable.ic_action_bar_close, (v) -> {
            finish();
        });
        binding.toolbar.setOnBackButtonClickListener(v -> {
            if (webView != null && webView.canGoBack()) {
                webView.goBack();
            } else {
                finish();
            }
        });
        initWebView();
    }

    public static void load(Context context, String url) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    private void initWebView() {
        webView = new WebView(this);
        WebSettings setting = webView.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setDomStorageEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                binding.toolbar.setTitle(title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                binding.progressBar.setProgress(newProgress);
                binding.progressBar.setVisibility(newProgress < binding.progressBar.getMax() ? View.VISIBLE : View.INVISIBLE);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.d("WebView", request.getUrl().toString());
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("WebView", "->"+url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        String url = getUrlFromBundle();
        if (TextUtils.isEmpty(url)) {
            webView.loadUrl("https://giveaway.com");
        } else {
            webView.loadUrl(url);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        params.weight = 1;
        webView.setLayoutParams(params);
        binding.llContainer.addView(webView);
    }

    private String getUrlFromBundle() {
        Intent intent = getIntent();
        Uri uri = intent.getData();
        if (uri != null) {
            return uri.toString();
        }
        return "";
    }

    @Override
    protected void onDestroy() {
        binding.llContainer.removeView(webView);
        webView = null;
        super.onDestroy();
    }
}

package com.hash.coinconvert.rnmodule.view;

import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Optional;

public class CustomWebViewClient extends WebViewClient {

    private Optional<ClientProxy> proxy;

    public CustomWebViewClient(ClientProxy proxy) {
        this.proxy = Optional.ofNullable(proxy);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        Log.d("CustomWebViewClient", "onPageStarted:" + url);
    }
}

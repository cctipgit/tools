package com.hash.coinconvert.rnmodule.view;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Optional;

public class CustomWebChromeClient extends WebChromeClient {

    private Optional<ClientProxy> proxy;

    public CustomWebChromeClient(ClientProxy proxy) {
        this.proxy = Optional.ofNullable(proxy);
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        Context context = view.getContext();

        WebView webView = new WebView(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(params);
        MultiWebView.initWebViewSetting(webView, null, null);
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setContentView(webView);
        dialog.show();
        updateWindow(dialog.getWindow());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onCloseWindow(WebView window) {
                dialog.dismiss();
            }
        });
        webView.setWebViewClient(new WebViewClient());

        if (resultMsg.obj instanceof WebView.WebViewTransport) {
            ((WebView.WebViewTransport) resultMsg.obj).setWebView(webView);
        }
        resultMsg.sendToTarget();
        return true;
    }

    private void updateWindow(Window window) {
        if (window == null) return;
        WindowManager.LayoutParams params = window.getAttributes();
        window.setLayout(-1, -1);
        FrameLayout fl = window.getDecorView().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        fl.getLayoutParams().width = -1;
        fl.getLayoutParams().height = -1;
        fl.invalidate();
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        proxy.ifPresent((p) -> {
            p.onTitle(title);
        });
    }
}

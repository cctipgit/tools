package com.hash;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hash.coinconvert.R;
import com.hash.coinconvert.widget.lottery.LotteryView;
import com.hash.coinconvert.widget.lottery.PointsComponent;

public class WebActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        WebView webView = new WebView(this);
        setContentView(R.layout.layout_test);
//        WebSettings setting = webView.getSettings();
//        setting.setJavaScriptEnabled(true);
//        setting.setDomStorageEnabled(true);
//        webView.setWebChromeClient(new WebChromeClient() {
//
//        });
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                Log.d("WebView", request.getUrl().toString());
//                return super.shouldOverrideUrlLoading(view, request);
//            }
//        });
//        webView.loadUrl("https://giveaway.com");
        LotteryView view=findViewById(R.id.lottery);
        findViewById(R.id.btn).setOnClickListener(v->{
            String id = String.valueOf((int)(Math.random()*5+1));
            Log.d(PointsComponent.TAG,"test io:"+id);
            view.startRotate(id);
        });
        findViewById(R.id.btn_reset).setOnClickListener(v->{
            view.reset();
        });
    }
}

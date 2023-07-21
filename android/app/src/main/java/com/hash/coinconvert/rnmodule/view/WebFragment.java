package com.hash.coinconvert.rnmodule.view;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.duxl.baselib.utils.DisplayUtil;
import com.hash.coinconvert.R;
import com.hash.coinconvert.databinding.FragmentRnWebBinding;

public class WebFragment extends Fragment {

    private WebView webView;
    private FragmentRnWebBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rn_web, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentRnWebBinding.bind(view);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String url = bundle.getString("url");
            setUrl(url);
        }
        test(webView);
    }

    private void test(View v) {
        ViewParent parent = v.getParent();
        if (parent != null && parent instanceof ViewGroup) {
            ViewGroup view = ((ViewGroup) parent);
            Log.d(WebViewViewManager.NAME, "-->" + view + ":" + view.getMeasuredWidth() + "," + view.getMeasuredHeight());
            test(((ViewGroup) parent));
        }
    }

    public void setUrl(String url) {
        Log.d(WebViewViewManager.NAME, "setUrl:" + url);
        ensureWebView();
        webView.loadUrl(url);
    }

    private void ensureWebView() {
        if (webView == null || webView.getParent() == null) {
            webView = new WebView(requireContext());
            initWebViewSetting(webView);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            webView.setLayoutParams(params);
            binding.flContainer.addView(webView);
            webView.setBackgroundColor(Color.GREEN);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.flContainer.removeAllViews();
        webView = null;
    }

    public static void initWebViewSetting(WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setUserAgentString(settings.getUserAgentString().replace("; wv", ""));
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(true);
//        webView.addJavascriptInterface(new JsBridge(), JsBridge.NAME);
//        webView.setWebChromeClient(new CustomWebChromeClient());
//        webView.setWebViewClient(new WebViewClient());
    }
}

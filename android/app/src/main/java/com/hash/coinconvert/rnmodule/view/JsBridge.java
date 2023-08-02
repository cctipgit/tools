package com.hash.coinconvert.rnmodule.view;

import android.util.Log;
import android.webkit.JavascriptInterface;

public class JsBridge {

    public static final String NAME = "jsbridge";

    private Evaluate evaluate;

    public JsBridge(Evaluate evaluate) {
        this.evaluate = evaluate;
    }

    @JavascriptInterface
    public void postMessage(String key,String message) {
        Log.d(NAME, "postMessage:" + message);
        if (evaluate != null) {
            evaluate.evaluateJs(key,message);
        }
    }
}

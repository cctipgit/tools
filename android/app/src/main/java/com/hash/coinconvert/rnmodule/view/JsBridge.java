package com.hash.coinconvert.rnmodule.view;

import android.util.Log;
import android.webkit.JavascriptInterface;

import java.util.Map;

public class JsBridge {

    public static final String NAME = "f_jsBridge";

    private Evaluate evaluate;

    public JsBridge(Evaluate evaluate) {
        this.evaluate = evaluate;
    }

    @JavascriptInterface
    public void postMessage(String key, String message) {
        Log.d(NAME, "postMessage:" + key + " , " + message);
        if (evaluate != null) {
            evaluate.evaluateJs(key, ""+message);
        }
    }
}

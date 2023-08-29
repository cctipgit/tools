package com.rate.quiz.rnmodule.view;

public interface ClientProxy {
    void onTitle(String title);

    void onProgress(int progress);

    void onCanGoBack(boolean canGoBack);
}

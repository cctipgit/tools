package com.rate.quiz.vm;

import com.rate.quiz.base.BaseViewModel;
import com.rate.quiz.http.RetrofitHelper;
import com.rate.quiz.http.api.ToolApi;

public class GameViewModel extends BaseViewModel {
    private ToolApi api;

    public GameViewModel() {
        api = RetrofitHelper.create(ToolApi.class);
    }

    public void pinCheck() {
        execute(api.pinCheck(), pinCheckResponse -> {
        });
    }

    public void pinList() {
        execute(api.pinList(), pinList -> {

        });
    }
}

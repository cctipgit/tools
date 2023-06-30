package com.hash.coinconvert.vm;

import com.hash.coinconvert.base.BaseViewModel;
import com.hash.coinconvert.http.RetrofitHelper;
import com.hash.coinconvert.http.api.ToolApi;

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

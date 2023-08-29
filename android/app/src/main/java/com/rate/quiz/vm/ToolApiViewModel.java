package com.rate.quiz.vm;

import com.rate.quiz.base.BaseViewModel;
import com.rate.quiz.http.RetrofitHelper;
import com.rate.quiz.http.api.ToolApi;

public class ToolApiViewModel extends BaseViewModel {
    protected ToolApi api;

    public ToolApiViewModel(){
        api = RetrofitHelper.create(ToolApi.class);
    }
}

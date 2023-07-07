package com.hash.coinconvert.vm;

import com.hash.coinconvert.base.BaseViewModel;
import com.hash.coinconvert.http.RetrofitHelper;
import com.hash.coinconvert.http.api.ToolApi;

public class ToolApiViewModel extends BaseViewModel {
    protected ToolApi api;

    public ToolApiViewModel(){
        api = RetrofitHelper.create(ToolApi.class);
    }
}

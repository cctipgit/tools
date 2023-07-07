package com.hash.coinconvert.vm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hash.coinconvert.base.BaseViewModel;
import com.hash.coinconvert.http.RetrofitHelper;
import com.hash.coinconvert.http.api.ToolApi;


public class LotteryViewModel extends BaseViewModel {
    private ToolApi api;

    private MutableLiveData<String> pinCheckId = new MutableLiveData<>();
    public LiveData<String> getPinCheckId(){
        return pinCheckId;
    }

    public LotteryViewModel(){
        this.api = RetrofitHelper.create(ToolApi.class);
    }

    public void pinCheck(){
        if(isNotLoading()) {
            startLoading();
            execute(api.pinCheck(), res -> {
                pinCheckId.postValue(res.id);
            });
        }
    }
}

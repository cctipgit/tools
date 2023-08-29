package com.rate.quiz.vm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rate.quiz.base.BaseViewModel;
import com.rate.quiz.entity.PinList;
import com.rate.quiz.http.RetrofitHelper;
import com.rate.quiz.http.api.ToolApi;


public class LotteryViewModel extends BaseViewModel {
    private ToolApi api;

    private MutableLiveData<String> pinCheckId = new MutableLiveData<>();
    public LiveData<String> getPinCheckId(){
        return pinCheckId;
    }

    private MutableLiveData<PinList> pinList = new MutableLiveData<>();

    public LiveData<PinList> getPinList(){
        return pinList;
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

    public void fetchPinList(){
        startLoadingIfNeeded(pinList);
        execute(api.pinList(),res -> {
            pinList.postValue(res);
        });
    }
}

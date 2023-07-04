package com.hash.coinconvert.vm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hash.coinconvert.base.BaseViewModel;
import com.hash.coinconvert.http.RetrofitHelper;
import com.hash.coinconvert.http.api.ToolApi;

import java.text.NumberFormat;

public class LotteryViewModel extends BaseViewModel {
    private ToolApi api;

    private MutableLiveData<String> pinCheckId = new MutableLiveData<>();
    private MutableLiveData<String> points = new MutableLiveData<>();
    public LiveData<String> getPinCheckId(){
        return pinCheckId;
    }

    public LiveData<String> getPoints(){
        return points;
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

    public void userInfo(){
        execute(api.userProfile(),user->{
            points.postValue(NumberFormat.getNumberInstance().format(user.point));
        });
    }
}

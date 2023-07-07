package com.hash.coinconvert.vm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hash.coinconvert.base.BaseViewModel;
import com.hash.coinconvert.entity.PageRequest;
import com.hash.coinconvert.entity.RedeemHistoryList;
import com.hash.coinconvert.http.RetrofitHelper;
import com.hash.coinconvert.http.api.ToolApi;

public class RedeemHistoryViewModel extends BaseViewModel {

    private MutableLiveData<RedeemHistoryList> histories = new MutableLiveData<>();

    public LiveData<RedeemHistoryList> getHistories() {
        return histories;
    }

    private ToolApi toolApi;

    public RedeemHistoryViewModel() {
        toolApi = RetrofitHelper.create(ToolApi.class);
    }

    public void fetch(int page){
        if(page == 0) {
            startLoading();
        }
        execute(toolApi.redeemHistory(PageRequest.of(page)),data->{
            histories.postValue(data);
        });
    }

    @Override
    public void onCreate() {
        fetch(0);
    }
}

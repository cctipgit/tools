package com.rate.quiz.vm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rate.quiz.base.BaseViewModel;
import com.rate.quiz.entity.PageRequest;
import com.rate.quiz.entity.RedeemHistoryList;
import com.rate.quiz.http.RetrofitHelper;
import com.rate.quiz.http.api.ToolApi;

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

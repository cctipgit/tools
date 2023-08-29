package com.rate.quiz.vm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rate.quiz.base.BaseViewModel;
import com.rate.quiz.entity.PageRequest;
import com.rate.quiz.entity.RedeemPointList;
import com.rate.quiz.http.RetrofitHelper;
import com.rate.quiz.http.api.ToolApi;

public class PointsDetailsViewModel extends BaseViewModel {

    private MutableLiveData<RedeemPointList> pointsList = new MutableLiveData<>();

    public LiveData<RedeemPointList> getPointsList() {
        return pointsList;
    }

    private ToolApi toolApi;

    public PointsDetailsViewModel() {
        toolApi = RetrofitHelper.create(ToolApi.class);
    }

    public void fetch(int page){
        if(page == 0) {
            startLoading();
        }
        execute(toolApi.redeemPointList(PageRequest.of(page)),data->{
            pointsList.postValue(data);
        });
    }

    @Override
    public void onCreate() {
        fetch(0);
    }
}

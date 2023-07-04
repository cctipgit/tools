package com.hash.coinconvert.vm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hash.coinconvert.base.BaseViewModel;
import com.hash.coinconvert.entity.PageRequest;
import com.hash.coinconvert.entity.RedeemPointList;
import com.hash.coinconvert.http.RetrofitHelper;
import com.hash.coinconvert.http.api.ToolApi;

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
}

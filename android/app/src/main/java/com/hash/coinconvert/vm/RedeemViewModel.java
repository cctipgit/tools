package com.hash.coinconvert.vm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hash.coinconvert.base.BaseViewModel;
import com.hash.coinconvert.entity.RedeemItem;
import com.hash.coinconvert.http.RetrofitHelper;
import com.hash.coinconvert.http.api.ToolApi;

import java.util.Collections;
import java.util.List;

public class RedeemViewModel extends BaseViewModel {
    private ToolApi api;

    private MutableLiveData<List<RedeemItem>> redeemList = new MutableLiveData<>();

    public LiveData<List<RedeemItem>> getRedeemList() {
        return redeemList;
    }

    public RedeemViewModel() {
        this.api = RetrofitHelper.create(ToolApi.class);
    }

    public void fetchRedeemCards() {
        execute(api.redeemList(), list -> {
            if (list.list == null) {
                list.list = Collections.emptyList();
            }
            redeemList.postValue(list.list);
        });
    }
}

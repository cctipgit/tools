package com.rate.quiz.vm;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rate.quiz.base.BaseViewModel;
import com.rate.quiz.database.entity.Token;
import com.rate.quiz.database.repository.TokenRepository;
import com.rate.quiz.entity.RedeemItem;
import com.rate.quiz.entity.RedeemRequest;
import com.rate.quiz.http.RetrofitHelper;
import com.rate.quiz.http.api.ToolApi;
import com.rate.quiz.utils.Dispatch;
import com.rate.quiz.utils.task.TaskHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedeemViewModel extends BaseViewModel {
    private ToolApi api;

    private Map<String, Token> tokenMap;

    private MutableLiveData<List<RedeemItem>> redeemList = new MutableLiveData<>();

    public LiveData<List<RedeemItem>> getRedeemList() {
        return redeemList;
    }

    public RedeemViewModel() {
        this.api = RetrofitHelper.create(ToolApi.class);
    }

    public void fetchRedeemCards() {
        startLoadingIfNeeded(redeemList);
        execute(api.redeemList(), list -> {
            Dispatch.I.submit(() -> {
                if (list.list == null) {
                    list.list = Collections.emptyList();
                }
                if (tokenMap == null) {
                    List<Token> tokens = TokenRepository.all();
                    tokenMap = new HashMap<>(tokens.size());
                    for (Token t : tokens) {
                        tokenMap.put(t.token, t);
                    }
                }
                for (RedeemItem item : list.list) {
                    Token t = tokenMap.get(item.symbol);
                    if (t != null) {
                        item.currencyType = t.currencyType;
                        item.unit = t.unitName;
                        if (TextUtils.isEmpty(item.pic)) {
                            item.pic = t.icon;
                        }
                    }
                }
                redeemList.postValue(list.list);
            });
        });
    }

    public void redeem(String id) {
        if (isNotLoading()) {
            startLoading();
            execute(api.redeem(RedeemRequest.of(id)), ignore -> {
                fetchRedeemCards();
                TaskHelper.redeem();
            });
        }
    }

    @Override
    public void onCreate() {
        fetchRedeemCards();
    }
}

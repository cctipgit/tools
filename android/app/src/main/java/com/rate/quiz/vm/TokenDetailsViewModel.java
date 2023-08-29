package com.rate.quiz.vm;

import android.annotation.SuppressLint;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.duxl.baselib.http.RetrofitManager;
import com.rate.quiz.base.BaseViewModel;
import com.rate.quiz.database.entity.Token;
import com.rate.quiz.database.repository.TokenRepository;
import com.rate.quiz.http.api.KLineApi;
import com.rate.quiz.ui2.fragment.TokenDetailsFragment;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TokenDetailsViewModel extends BaseViewModel {

    public static final int TYPE_CURRENCY = 1;
    public static final int TYPE_DIGITAL = 2;

    private MutableLiveData<TokenDetailsFragment.UIData> data = new MutableLiveData<>();

    public LiveData<TokenDetailsFragment.UIData> getUIData() {
        return data;
    }

    private final KLineApi api;

    public TokenDetailsViewModel() {
        api = RetrofitManager.getInstance().create(KLineApi.class);
    }

    public void fetchData(TokenDetailsFragment.UIData ui, String range) {
        startLoading();
        data.postValue(ui);

        execute(api.kLineData(range, ui.base.token + "/" + ui.quote.token), data -> {
            TokenDetailsFragment.UIData uiData = new TokenDetailsFragment.UIData();
            uiData.data = data.series;
            this.data.postValue(uiData);
        });
    }

    @SuppressLint("CheckResult")
    public void fetchData(String base, String quote, String range) {
        if (Boolean.TRUE.equals(isLoading().getValue())) return;
        startLoading();
        Observable<TokenDetailsFragment.UIData> observable = Observable.create(sub -> {
            TokenDetailsFragment.UIData uiData = new TokenDetailsFragment.UIData();
            uiData.base = queryBySymbol(base);
            uiData.quote = queryBySymbol(quote);
            sub.onNext(uiData);
            sub.onComplete();
        });
        observable.subscribeOn(Schedulers.computation())
                .subscribe(uiData -> fetchData(uiData, range));
    }

    private Token queryBySymbol(String token) {
        Token t = TokenRepository.findBySymbol(token);
        if (t == null) {
            throw new IllegalArgumentException(String.format("%s not found", token));
        }
        return t;
    }

    private boolean isCurrency(Token token) {
        return Token.TOKEN_TYPE_CURRENCY.equals(token.currencyType);
    }

    private int getCurrencyTypeAsInt(Token token) {
        return isCurrency(token) ? TYPE_CURRENCY : TYPE_DIGITAL;
    }

}

package com.hash.coinconvert.vm;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.duxl.baselib.utils.SPUtils;
import com.hash.coinconvert.Constants;
import com.hash.coinconvert.base.BaseViewModel;
import com.hash.coinconvert.database.DBHolder;
import com.hash.coinconvert.database.entity.Token;
import com.hash.coinconvert.database.repository.TokenRepository;
import com.hash.coinconvert.entity.TokenWrapper;
import com.hash.coinconvert.http.SocketSender;

import java.util.List;
import java.util.stream.Collectors;

public class CurrencyViewModel extends BaseViewModel {
    private MutableLiveData<List<TokenWrapper>> currencies = new MutableLiveData<>();

    public MutableLiveData<List<TokenWrapper>> getCurrencies() {
        return currencies;
    }

    private static final String[] DEFAULT_TOKENS = new String[]{"BTC", "USDT", "DOGE", "ETH"};

    public void loadAllCurrencyInfo() {
        List<Token> tokens = TokenRepository.getFavorites();
        currencies.postValue(tokens.stream().map(TokenWrapper::new).collect(Collectors.toList()));
        List<String> tokenSymbols = tokens.stream().map(it -> it.token).collect(Collectors.toList());
        String tokensString = "[" +
                tokenSymbols.stream().reduce((s, s2) -> s + "," + s2).get()
                + "]";
        SPUtils.getInstance().put(Constants.SP.KEY.HOME_TOKEN_LIST, tokensString);
    }

    public void fetchPrices() {
        SocketSender.sendSymbolsRateRequest(SPUtils.getInstance().getString(Constants.SP.KEY.HOME_TOKEN_LIST));
    }

    private int indexOfDefaultTokens(String token) {
        for (int i = 0; i < DEFAULT_TOKENS.length; i++) {
            if (DEFAULT_TOKENS[i].equals(token)) return i;
        }
        return -1;
    }
}

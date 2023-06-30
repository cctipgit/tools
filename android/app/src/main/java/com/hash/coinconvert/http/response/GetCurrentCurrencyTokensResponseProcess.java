package com.hash.coinconvert.http.response;

import android.annotation.SuppressLint;

import com.duxl.baselib.utils.EmptyUtils;
import com.duxl.baselib.utils.SPUtils;
//import com.exchange2currency.ef.currencyprice.grpc.Price;
import com.exchange2currency.ef.currencyconvert.grpc.Price;
import com.google.gson.Gson;
import com.hash.coinconvert.Constants;
import com.hash.coinconvert.livedatabus.LiveDataKey;
import com.hash.coinconvert.utils.Dispatch;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 获取当前位置的初始货币响应处理
 */
public class GetCurrentCurrencyTokensResponseProcess {

    public static <T> void process(ByteBuffer bytes, T data) {
        Dispatch.I.submit(new GetCurrentCurrencyTokensResponseProcess.ProcessRunnable(bytes));

    }

    private static class ProcessRunnable implements Runnable {
        private ByteBuffer bytes;

        public ProcessRunnable(ByteBuffer bytes) {
            this.bytes = bytes;
        }

        @SuppressLint("NewApi")
        @Override
        public void run() {
            try {
                Price.GetCurrentCurrencyTokensResponse tokensResponse = Price.GetCurrentCurrencyTokensResponse.parseFrom(bytes);
                List<Price.GetCurrencyTokensResponse> dataList = tokensResponse.getDataList();
                if (EmptyUtils.isNotEmpty(dataList)) {
                    List<String> tokens = dataList.stream().map(it -> it.getToken()).collect(Collectors.toList());
                    String tokenJson = new Gson().toJson(tokens);
                    SPUtils.getInstance().put(Constants.SP.KEY.HOME_TOKEN_LIST, tokenJson);
                    if (EmptyUtils.isEmpty(SPUtils.getInstance().getString(Constants.SP.KEY.FREQUENTLY_TOKEN_LIST))) {
                        SPUtils.getInstance().put(Constants.SP.KEY.FREQUENTLY_TOKEN_LIST, tokenJson);
                    }

                    String countryCode = SPUtils.getInstance().getString(Constants.SP.KEY.LOCATION_COUNTRY_CODE, null);
                    if(EmptyUtils.isEmpty(countryCode)) {
                        SPUtils.getInstance().put(Constants.SP.KEY.LOCATION_COUNTRY_CODE, dataList.get(0).getCountryCode());
                    }

                    LiveEventBus.get(LiveDataKey.HOME_TOKEN_LIST_CHANGED).post(null);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

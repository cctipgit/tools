package com.hash.coinconvert.http.response;

import static com.hash.coinconvert.livedatabus.LiveDataKey.SYMBOLS_RATE_CHANGED;

import android.util.Log;

import androidx.lifecycle.ProcessLifecycleOwner;

import com.duxl.baselib.utils.EmptyUtils;
import com.duxl.baselib.utils.SPUtils;
//import com.exchange2currency.ef.currencyprice.grpc.Price;
import com.exchange2currency.ef.currencyconvert.grpc.Price;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.hash.coinconvert.Constants;
import com.hash.coinconvert.database.CurrencyDaoWrap;
import com.hash.coinconvert.database.repository.TokenRepository;
import com.hash.coinconvert.entity.CurrencyInfo;
import com.hash.coinconvert.http.SocketSender;
import com.hash.coinconvert.livedatabus.event.SymbolsRateEvent;
import com.hash.coinconvert.utils.Dispatch;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 收到一个币对的价格比数据处理逻辑
 */
public class GetSymbolsRateResponseProcess {

    private static CurrencyDaoWrap mCurrencyDaoWrap = new CurrencyDaoWrap(ProcessLifecycleOwner.get().getLifecycle());

    public static <T> void process(ByteBuffer bytes, T data) {
        Dispatch.I.submit(new GetSymbolsRateResponseProcess.ProcessRunnable(bytes));
    }

    public static void getRequest() {
        Dispatch.I.submit(new GetRequestRunnable());
    }

    /**
     * 收到汇率信息处理
     */
    private static class ProcessRunnable implements Runnable {
        private ByteBuffer bytes;

        public ProcessRunnable(ByteBuffer bytes) {
            this.bytes = bytes;
        }

        @Override
        public void run() {
            try {
                Price.GetSymbolsRateResponse response = Price.GetSymbolsRateResponse.parseFrom(bytes);
                Map<String, Price.GetSymbolsRateItem> dataMap = response.getDataMap();
                if (EmptyUtils.isEmpty(dataMap)) {
                    return;
                }
                for (Price.GetSymbolsRateItem item : dataMap.values()) {
//                    CurrencyInfo currency = new CurrencyInfo();
//                    currency.token = item.getToken();
//                    currency.price = item.getPrice();
//                    list.add(currency);
                    Log.d("Update", item.getToken() + ":" + item.getPrice());
                    TokenRepository.updatePrice(item.getToken(), item.getPrice());
                }
//                mCurrencyDaoWrap.updatePrice(list);
//
//                LiveEventBus.get(SYMBOLS_RATE_CHANGED).post(new SymbolsRateEvent(list));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class GetRequestRunnable implements Runnable {
        @Override
        public void run() {
            String tokenJson = SPUtils.getInstance().getString(Constants.SP.KEY.HOME_TOKEN_LIST);
            if (EmptyUtils.isNotEmpty(tokenJson)) {
                List<String> tokens = new Gson().fromJson(tokenJson, new TypeToken<List<String>>() {
                }.getType());
                SocketSender.sendSymbolsRateRequest(String.join(",", tokens));

            }
        }
    }
}

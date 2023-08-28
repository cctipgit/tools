package com.hash.coinconvert.http.response;

import androidx.lifecycle.ProcessLifecycleOwner;

import com.duxl.baselib.utils.EmptyUtils;
import com.duxl.baselib.utils.SPUtils;
import com.exchange2currency.ef.currencyconvert.grpc.Price;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.hash.coinconvert.Constants;
import com.hash.coinconvert.database.CurrencyDaoWrap;
import com.hash.coinconvert.database.repository.TokenRepository;
import com.hash.coinconvert.http.SocketSender;
import com.hash.coinconvert.utils.Dispatch;

import java.nio.ByteBuffer;
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
                    TokenRepository.updatePrice(item.getToken(), item.getPrice());
                }
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

package com.hash.coinconvert.http.response;

import androidx.lifecycle.ProcessLifecycleOwner;

import com.duxl.baselib.utils.EmptyUtils;
//import com.exchange2currency.ef.currencyprice.grpc.Price;
import com.exchange2currency.ef.currencyconvert.grpc.Price;
import com.hash.coinconvert.database.CurrencyDaoWrap;
import com.hash.coinconvert.entity.CurrencyInfo;
import com.hash.coinconvert.http.SocketSender;
import com.hash.coinconvert.livedatabus.LiveDataKey;
import com.hash.coinconvert.utils.Dispatch;
import com.hash.coinconvert.utils.FlagLoader;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * 收到货币类型列表数据处理
 */
public class GetCurrencyTokensListResponseProcess {

    private static CurrencyDaoWrap mCurrencyDaoWrap = new CurrencyDaoWrap(ProcessLifecycleOwner.get().getLifecycle());

    public static <T> void process(ByteBuffer bytes, T data) {
        Dispatch.I.submit(new ProcessRunnable(bytes));
    }

    /**
     * 检测本地货币列表是否同步更新
     */
    public static void checkAndSynchronize() {
        Dispatch.I.submit(new CheckAndSynchronize());
    }

    private static class ProcessRunnable implements Runnable {
        private ByteBuffer bytes;

        public ProcessRunnable(ByteBuffer bytes) {
            this.bytes = bytes;
        }

        @Override
        public void run() {
            try {
                Price.GetCurrencyTokensListResponse response = Price.GetCurrencyTokensListResponse.parseFrom(bytes);
                List<Price.GetCurrencyTokensListMap> dataList = response.getDataList();
                if (EmptyUtils.isNotEmpty(dataList)) {
                    List<CurrencyInfo> currencyList = new ArrayList<>();
                    for (Price.GetCurrencyTokensListMap listMap : dataList) {
                        String fchat = listMap.getFchat();
                        List<Price.GetCurrencyTokensResponse> tokens = listMap.getDataList();
                        if (EmptyUtils.isNotEmpty(tokens)) {
                            for (Price.GetCurrencyTokensResponse token : tokens) {
                                CurrencyInfo currency = new CurrencyInfo();
                                currency.token = token.getToken();
                                currency.icon = token.getIcon();
                                currency.name = token.getName();
                                currency.currencyType = token.getCurrencyType();
                                currency.unitName = token.getUnitName();
                                currency.countryCode = token.getCountryCode();
                                currency.fChat = fchat;
                                currency.price = token.getPrice();
                                currencyList.add(currency);
                            }
                        }
                    }
                    // 删除本地就的货币信息
                    mCurrencyDaoWrap.delete(currencyList);
                    // 添加最新的货币信息
                    mCurrencyDaoWrap.insert(currencyList);
                    // 广播token数据已更新
                    LiveEventBus.get(LiveDataKey.CURRENCY_TOKENS_LIST_CHANGED).post(null);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class CheckAndSynchronize implements Runnable {
        @Override
        public void run() {
            List<CurrencyInfo> currencyAll = mCurrencyDaoWrap.getCurrencyAll();
            if(EmptyUtils.isNotEmpty(currencyAll)) {
                String icon = currencyAll.get(0).icon;
                if(!FlagLoader.isNetworkImage(icon)) {
                    SocketSender.sendCurrencyTokensList();
                }
            }
        }
    }
}

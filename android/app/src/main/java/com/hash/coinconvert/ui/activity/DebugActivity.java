package com.hash.coinconvert.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

//import com.exchange2currency.ef.currencyprice.grpc.Price;
//import com.exchange2currency.ef.currencyconvert.grpc.Price;
//import com.exchange2currency.ef.currencyprice.grpc.Price;
import com.exchange2currency.ef.currencyconvert.grpc.Price;
import com.google.gson.Gson;
import com.hash.coinconvert.R;
//import com.hash.exchangerate.http.SocketSender;
import com.hash.coinconvert.http.SocketSender;
import com.hash.coinconvert.utils.DateUtil;
import com.zhangke.websocket.SimpleListener;
import com.zhangke.websocket.SocketListener;
import com.zhangke.websocket.WebSocketHandler;
import com.zhangke.websocket.response.ErrorResponse;

import org.java_websocket.framing.Framedata;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.nio.ByteBuffer;

public class DebugActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebSocketHandler.getDefault().addListener(socketListener);
//        WebSocketHandler.getDefault().addListener(socketListener1);

    }

    @Override
    protected void initView(View v) {
        super.initView(v);
        setTitle("Debug");
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_debug;
    }

//    public void getKlinePage(View view){
//        System.out.println("----RateKlineAc----");
//        Intent goIntent = new Intent(getContext(),RateKlineAc.class);
//        startActivity(goIntent);
////        goActivity(RateKlineAc.class);
//    }

    public void getKlineData(View view) {
        Long startTime = new BigDecimal(DateUtil.date2TimeStamp("2023-01-05 23:18:24", "yyyy-MM-dd HH:mm:ss")).longValue();
        Long endTime = new BigDecimal(DateUtil.date2TimeStamp("2023-01-04 23:18:24", "yyyy-MM-dd HH:mm:ss")).longValue();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("start_time", startTime);
            jsonObject.put("end_time", endTime);
            jsonObject.put("type", 2);
            jsonObject.put("symbol", "BTCETH");
            jsonObject.put("cid", DateUtil.getRandom(10));
            jsonObject.put("cmd", 15);
            System.out.println("========req data======" + jsonObject.toString());
            SocketSender.sendGetKLineRequest(DateUtil.getRandom(10) + "", "BTCETH", 2, startTime, endTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取行情列表
     *
     * @param view
     */
    public void onGetQuotation(View view) {
        SocketSender.sendGetQuotationRequest("USD", "CNY", "24H", "");
    }

    /**
     * 获取行情列表Stop
     *
     * @param view
     */
    public void onGetQuotationStop(View view) {
        SocketSender.sendGetQuotationStopRequest();
    }

    /**
     * 获取一个币对的价格比
     *
     * @param view
     */
    public void getSymbolsRateRequest(View view) {
        SocketSender.sendSymbolsRateRequest("USD,BTC,USDT,DOGE,ETH,FJD");
    }

    //***********************************************************************

    /**
     * 获取当前位置的初始5个货币
     *
     */
    @Deprecated
    public void defaultCurrency(View v) {
        byte[] bytes = Price.GetDefaultCurrencyTokensListRequest.newBuilder()
                .setCmd(13)
                .setCid(DateUtil.getRandom(10) + "")
                .build().toByteArray();
        WebSocketHandler.getDefault().send(bytes);
        log("发送请求：GetDefaultCurrencyTokensListRequest");
    }

    /**
     * 获取当前位置默认货币
     *
     * @param v
     */
    public void currentDefaultCurrency(View v) {
        SocketSender.sendCurrentDefaultCurrency(6, "CN");
    }

    //***********************************************************************

    /**
     * 获取货币类型
     *
     * @param v
     * @deprecated
     */
    @Deprecated
    public void getCurrencyTokens(View v) {
        byte[] bytes = Price.GetCurrencyTokensRequest.newBuilder()
                .setCmd(7)
                .setCid(DateUtil.getRandom(10) + "")
                .setToken("CNY")
                .build().toByteArray();
        WebSocketHandler.getDefault().send(bytes);
        log("发送请求：GetCurrencyTokensRequest");
    }

    /**
     * 获取货币类型列表
     *
     * @param v
     */
    public void getCurrencyTokensList(View v) {
        SocketSender.sendCurrencyTokensList();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        WebSocketHandler.getDefault().removeListener(socketListener);
    }

    private SocketListener socketListener = new SimpleListener() {
        @Override
        public void onConnected() {
            super.onConnected();
            log("onConnected 连接成功");
        }

        @Override
        public void onConnectFailed(Throwable e) {
            super.onConnectFailed(e);
            log("onConnectFailed 连接失败");
        }

        @Override
        public void onDisconnect() {
            super.onDisconnect();
            log("onDisconnect");
        }

        @Override
        public <T> void onMessage(String message, T data) {
            super.onMessage(message, data);
            log("onMessage(message)");
        }

        @Override
        public void onSendDataError(ErrorResponse errorResponse) {
            super.onSendDataError(errorResponse);
            log("onSendDataError");
        }

        @Override
        public <T> void onMessage(ByteBuffer bytes, T data) {
            super.onMessage(bytes, data);
            log("onMessage(bytes)");
            try {
                Price.CommandHead commandHead = Price.CommandHead.parseFrom(bytes);
                log("收到响应cmd：" + commandHead.getCmd());
                switch (commandHead.getCmd()) {
                    case 15://kline行情
                        Price.GetKLineResponse getQuotationResponse1 = Price.GetKLineResponse.parseFrom(bytes);
                        Gson gson9 = new Gson();
// 对象 -->json
                        String getQuotationResponseJson9 = gson9.toJson(getQuotationResponse1);
//                        String getQuotationResponseJson1 = JsonFormat.printer().print(getQuotationResponse1);
                        log("kline收到行情应数据: \n" + getQuotationResponseJson9);
                        break;
                    case 2: // 行情数据
                        Price.GetQuotationResponse getQuotationResponse = Price.GetQuotationResponse.parseFrom(bytes);
                        Gson gson = new Gson();
// 对象 -->json
                        String getQuotationResponseJson = gson.toJson(getQuotationResponse);
//                        String getQuotationResponseJson = JsonFormat.printer().print(getQuotationResponse);
                        log("收到行情应数据: \n" + getQuotationResponseJson);
                        break;
                    case 4:
                        Price.GetQuotationStopResponse getQuotationStopResponse = Price.GetQuotationStopResponse.parseFrom(bytes);
                        Gson gsonResponse7 = new Gson();
// 对象 -->json
                        String getQuotationResp7 = gsonResponse7.toJson(getQuotationStopResponse);
//                        String getQuotationStopResponseJson = JsonFormat.printer().print(getQuotationStopResponse);
                        log("收到行情应Stop数据: \n" + getQuotationResp7);
                        break;

                    case 6:
                        Price.GetSymbolsRateResponse getSymbolsRateResponse = Price.GetSymbolsRateResponse.parseFrom(bytes);
                        Gson gsonResponse = new Gson();
// 对象 -->json
                        String getQuotationResp = gsonResponse.toJson(getSymbolsRateResponse);
//                        String getSymbolsRateResponseJson = JsonFormat.printer().print(getSymbolsRateResponse);
                        log("收到一个币对的价格比数据：\n" + getQuotationResp);
                        break;

                    case 14:
                        Price.GetDefaultCurrencyTokensListResponse getDefaultCurrencyTokensListResponse = Price.GetDefaultCurrencyTokensListResponse.parseFrom(bytes);
                        Gson gsonResponse1 = new Gson();
// 对象 -->json
                        String getQuotationResp1 = gsonResponse1.toJson(getDefaultCurrencyTokensListResponse);
//                        String getDefaultCurrencyTokensListResponseJson = JsonFormat.printer().print(getDefaultCurrencyTokensListResponse);
                        log("收到默认货币数据：\n" + getQuotationResp1);
                        break;
                    case 12:
                        Price.GetCurrentCurrencyTokensResponse getCurrentCurrencyTokensResponse = Price.GetCurrentCurrencyTokensResponse.parseFrom(bytes);
                        Gson gsonResponse6 = new Gson();
// 对象 -->json
                        String getQuotationResp6 = gsonResponse6.toJson(getCurrentCurrencyTokensResponse);
//                        String getCurrentCurrencyTokensResponseJson = JsonFormat.printer().print(getCurrentCurrencyTokensResponse);
                        log("收到当前位置默认货币数据: \n" + getQuotationResp6);
                        break;

                    case 8:
                        Price.GetCurrencyTokensResponse getCurrencyTokensResponse = Price.GetCurrencyTokensResponse.parseFrom(bytes);
                        Gson gsonResponse5 = new Gson();
// 对象 -->json
                        String getQuotationResp5 = gsonResponse5.toJson(getCurrencyTokensResponse);
//                        String getCurrencyTokensResponseJson = JsonFormat.printer().print(getCurrencyTokensResponse);
                        log("收到货币类型数据: \n" + getQuotationResp5);
                        break;
                    case 10:
                        Price.GetCurrencyTokensListResponse getCurrencyTokensListResponse = Price.GetCurrencyTokensListResponse.parseFrom(bytes);
                        Gson gsonResponse4 = new Gson();
// 对象 -->json
                        String getQuotationResp4 = gsonResponse4.toJson(getCurrencyTokensListResponse);
//                        String getCurrencyTokensListResponseJson = JsonFormat.printer().print(getCurrencyTokensListResponse);
                        log("收到货币类型列表数据: \n" + getQuotationResp4);
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPing(Framedata framedata) {
            super.onPing(framedata);
            log("发送 ping");
        }

        @Override
        public void onPong(Framedata framedata) {
            super.onPong(framedata);
            log("发送 pong");
        }
    };


    private void log(String msg) {
        Log.d("DebugActivity", msg);
    }
}


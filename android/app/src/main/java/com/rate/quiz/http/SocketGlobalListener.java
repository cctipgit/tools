package com.rate.quiz.http;

import android.util.Log;

//import com.exchange2currency.ef.currencyprice.grpc.Price;
import com.exchange2currency.ef.currencyconvert.grpc.Price;
import com.rate.quiz.http.response.GetCurrencyTokensListResponseProcess;
import com.rate.quiz.http.response.GetCurrentCurrencyTokensResponseProcess;
import com.rate.quiz.http.response.GetSymbolsRateResponseProcess;
import com.rate.quiz.utils.Dispatch;
import com.zhangke.websocket.SocketListener;
import com.zhangke.websocket.WebSocketHandler;
import com.zhangke.websocket.response.ErrorResponse;

import org.java_websocket.framing.Framedata;

import java.nio.ByteBuffer;

public class SocketGlobalListener implements SocketListener {

    private final String TAG = "SocketGlobalListener";

    @Override
    public void onConnected() {
        log("onConnected");
        // sock重新连接成功后，需要重新获取下汇率，否则服务端不会推送汇率数据，首页就不会更新
        GetSymbolsRateResponseProcess.getRequest();
        GetCurrencyTokensListResponseProcess.checkAndSynchronize();
    }

    @Override
    public void onConnectFailed(Throwable e) {
        log("onConnectFailed");
    }

    @Override
    public void onDisconnect() {
        log("onDisconnect");
        Dispatch.I.postDelayed(() -> {
            if (!WebSocketHandler.getDefault().isConnect()) {
                Log.i(TAG, "尝试重连");
                WebSocketHandler.getDefault().reconnect();
            }
        }, 1000);
    }

    @Override
    public <T> void onMessage(String message, T data) {
        log("onMessage(String)");
    }

    @Override
    public void onSendDataError(ErrorResponse errorResponse) {
        log("onSendDataError");
    }

    @Override
    public <T> void onMessage(ByteBuffer bytes, T data) {
        log("onMessage(bytes)");
        try {
            Price.CommandHead commandHead = Price.CommandHead.parseFrom(bytes);
            log("收到响应cmd：" + commandHead.getCmd());
            switch (commandHead.getCmd()) {
                case 2: // 行情数据
                   /* Price.GetQuotationResponse getQuotationResponse = Price.GetQuotationResponse.parseFrom(bytes);
                    String getQuotationResponseJson = JsonFormat.printer().print(getQuotationResponse);
                    log("收到行情应数据: \n" + getQuotationResponseJson);*/
                    break;
                case 4:
                    /*Price.GetQuotationStopResponse getQuotationStopResponse = Price.GetQuotationStopResponse.parseFrom(bytes);
                    String getQuotationStopResponseJson = JsonFormat.printer().print(getQuotationStopResponse);
                    log("收到行情应Stop数据: \n" + getQuotationStopResponseJson);*/
                    break;

                case 6:
                    /*Price.GetSymbolsRateResponse getSymbolsRateResponse = Price.GetSymbolsRateResponse.parseFrom(bytes);
                    String getSymbolsRateResponseJson = JsonFormat.printer().print(getSymbolsRateResponse);
                    log("收到一个币对的价格比数据：\n" + getSymbolsRateResponseJson);*/
                    GetSymbolsRateResponseProcess.process(bytes, data);
                    break;

                case 14:
                    /*Price.GetDefaultCurrencyTokensListResponse getDefaultCurrencyTokensListResponse = Price.GetDefaultCurrencyTokensListResponse.parseFrom(bytes);
                    String getDefaultCurrencyTokensListResponseJson = JsonFormat.printer().print(getDefaultCurrencyTokensListResponse);
                    log("收到默认货币数据：\n" + getDefaultCurrencyTokensListResponseJson);*/
                    break;
                case 12:
                    /*Price.GetCurrentCurrencyTokensResponse getCurrentCurrencyTokensResponse = Price.GetCurrentCurrencyTokensResponse.parseFrom(bytes);
                    String getCurrentCurrencyTokensResponseJson = JsonFormat.printer().print(getCurrentCurrencyTokensResponse);
                    log("收到当前位置默认货币数据: \n" + getCurrentCurrencyTokensResponseJson);*/
                    GetCurrentCurrencyTokensResponseProcess.process(bytes, data);
                    break;

                case 8:
                    /*Price.GetCurrencyTokensResponse getCurrencyTokensResponse = Price.GetCurrencyTokensResponse.parseFrom(bytes);
                    String getCurrencyTokensResponseJson = JsonFormat.printer().print(getCurrencyTokensResponse);
                    log("收到货币类型数据: \n" + getCurrencyTokensResponseJson);*/
                    break;
                case 10:
                    /*Price.GetCurrencyTokensListResponse getCurrencyTokensListResponse = Price.GetCurrencyTokensListResponse.parseFrom(bytes);
                    String getCurrencyTokensListResponseJson = JsonFormat.printer().print(getCurrencyTokensListResponse);
                    log("收到货币类型列表数据: \n" + getCurrencyTokensListResponseJson);*/
                    GetCurrencyTokensListResponseProcess.process(bytes, data);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPing(Framedata framedata) {
        log("onPing");
    }

    @Override
    public void onPong(Framedata framedata) {
        log("onPong");
    }

    private void log(String msg) {
        Log.i(TAG, msg);
    }
}

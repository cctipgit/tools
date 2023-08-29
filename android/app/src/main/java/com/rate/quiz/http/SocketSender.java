package com.rate.quiz.http;

import android.util.Log;

import com.duxl.baselib.utils.NullUtils;
//import com.exchange2currency.ef.currencyprice.grpc.Price;
//import com.exchange2currency.ef.currencyconvert.grpc.Price;
//import com.exchange2currency.ef.currencyconvert.grpc.Price;
//import com.exchange2currency.ef.currencyprice.grpc.Price;
//import com.exchange2currency.ef.currencyprice.grpc.Price;
import com.exchange2currency.ef.currencyconvert.grpc.Price;
import com.google.gson.Gson;
import com.google.protobuf.GeneratedMessageV3;
import com.rate.quiz.utils.DateUtil;
import com.zhangke.websocket.WebSocketHandler;

/**
 * socket发送数据类
 */
public class SocketSender {

    private static final String TAG = "SocketSender";

    private SocketSender() {

    }
    /**
     * 请求获取Kline行情
     *
     * @param cid          随机数 （长度int32）
     * @param symbol       如：BTCETH
     * @param type         时间单位  1, 1hour  2, 1day 一小时=1， 1天等于=2
     * @param start_time   语言 utc time , 毫秒 （毫秒的时间戳）
     * @param end_time     语言  ust time, 0: to now , 毫秒 （毫秒的时间戳）
     */
    public static void sendGetKLineRequest(String cid,String symbol,int type, long start_time,long end_time){
        Price.GetKLineRequest message =  Price.GetKLineRequest.newBuilder()
                .setCmd(15)
                .setCid(DateUtil.getRandom(10)+"")
                .setCid(cid)
                .setSymbol(symbol)
                .setType(type)
                .setStartTime(start_time)
                .setEndTime(end_time)
                .build();
        send(message);
    }
    /**
     * 请求获取行情列表
     *
     * @param tokenFrom
     * @param tokenTo
     * @param dateUnit  时间单位
     * @param lang      语言
     */
    public static void sendGetQuotationRequest(String tokenFrom, String tokenTo, String dateUnit, String lang) {
        Price.GetQuotationRequest message = Price.GetQuotationRequest.newBuilder()
                .setCmd(1)
                .setCid(DateUtil.getRandom(10)+"")
                .setTokenFrom(tokenFrom)
                .setTokenTo(tokenTo)
                .setDateUnit(dateUnit)
                .setLang(NullUtils.format(lang).toString())
                .build();
        send(message);

    }


    /**
     * 获取行情列表Stop
     */
    public static void sendGetQuotationStopRequest() {
        Price.GetQuotationStopRequest message = Price.GetQuotationStopRequest.newBuilder()
                .setCmd(3)
                .setCid(DateUtil.getRandom(10)+"")
                .build();
        send(message);
    }


    /**
     * 获取一个币对的价格比
     * @param tokens 例如：USD,BTC,USDT,DOGE,ETH,FJD
     */
    public static void sendSymbolsRateRequest(String tokens) {
        Price.GetSymbolsRateRequest message = Price.GetSymbolsRateRequest.newBuilder()
                .setCmd(5)
                .setCid(DateUtil.getRandom(10)+"")
                .setTokens(tokens)
                .build();
        send(message);
    }


    /**
     * 获取当前位置默认货币
     * @param count 数量
     * @param location 位置，例如：CN
     */
    public static void sendCurrentDefaultCurrency(int count, String location) {
        Price.GetCurrentCurrencyTokensRequest message = Price.GetCurrentCurrencyTokensRequest.newBuilder()
                .setCmd(11)
                .setCount(count)
                .setCid(DateUtil.getRandom(10)+"")
                .setLocation(NullUtils.format(location).toString())
                .build();
        send(message);
    }

    /**
     * 获取货币类型列表
     */
    public static void sendCurrencyTokensList() {
        Price.GetCurrencyTokensListRequest message = Price.GetCurrencyTokensListRequest.newBuilder()
                .setCmd(9)
                .setCid(DateUtil.getRandom(10)+"")
                .build();
        send(message);
    }

    /**
     * 统一消息发送
     *
     * @param message
     */
    private static void send(GeneratedMessageV3 message) {
        try {
            Gson gson = new Gson();
// 对象 -->json
            String sendJson = gson.toJson(message);
//            String sendJson = JsonFormat.printer().print(message);
            Log.d(TAG, "send socket data : " + sendJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
        WebSocketHandler.getDefault().send(message.toByteArray());
    }
}


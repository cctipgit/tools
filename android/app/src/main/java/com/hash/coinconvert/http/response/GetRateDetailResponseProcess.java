package com.hash.coinconvert.http.response;

import android.content.Context;
import android.util.Log;

import com.duxl.baselib.utils.SPUtils;
import com.exchange2currency.ef.currencyconvert.grpc.Price;
import com.google.gson.Gson;
import com.hash.coinconvert.Constants;
import com.hash.coinconvert.database.QuotesDataBase;
import com.hash.coinconvert.entity.QuotesItem;
import com.hash.coinconvert.entity.QuotesItemChild;
import com.hash.coinconvert.http.SocketSender;
import com.hash.coinconvert.livedatabus.LiveDataKey;
import com.hash.coinconvert.quotes.RateQuotesUtils;
import com.hash.coinconvert.ui.activity.RateDetailActivity;
import com.hash.coinconvert.utils.DateUtil;
//import com.hash.coinconvert.utils.Dispatch;
import com.hash.coinconvert.utils.Dispatch;
//import com.hash.coinconvert.utils.QuoteDispatch;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class GetRateDetailResponseProcess {

    public static <T> Runnable process(ByteBuffer bytes, Context context) {
        Dispatch.I.postDelayed(new ProcessRunnable(bytes,context),0);
        return new ProcessRunnable(bytes,context);
    }

    private static class ProcessRunnable implements Runnable {
        private ByteBuffer bytes;
        private Context context;
        private String tokenFrom;
        private String tokenTo;
        private int mId;

        public ProcessRunnable(ByteBuffer bytes,Context context) {
            this.bytes = bytes;
            this.context = context;
        }

        @Override
        public void run() {
            try {
                Price.CommandHead commandHead = Price.CommandHead.parseFrom(bytes);
                Log.d("CommandHead cmd：" , String.valueOf(commandHead.getCmd()));
                switch (commandHead.getCmd()) {
                    case 2:
                        Price.GetQuotationResponse getQuotationResponse = Price.GetQuotationResponse.parseFrom(bytes);
                        tokenFrom = getQuotationResponse.getTokenFrom();
                        tokenTo = getQuotationResponse.getTokenTo();
                        if (getQuotationResponse.getIsReal()){
                            Gson gson = new Gson();
                            ArrayList<QuotesItem> queryQuotesAllList = new ArrayList<>();
                            ArrayList<QuotesItem> quotesItemOldArray = QuotesDataBase.queryQuotesListByCategoryName(this.context ,this.tokenFrom,this.tokenTo,SPUtils.getInstance().getString(Constants.SP.KEY.TIME_TYPE,"24H"));
                            if (getQuotationResponse.getDataList().size() > 0){
                                if (quotesItemOldArray.size() > 0){
                                    mId = quotesItemOldArray.size();
                                    QuotesItem quotesFirstItem = quotesItemOldArray.get(0);
                                    queryQuotesAllList = RateQuotesUtils.updateQuotesCurrentList(getQuotationResponse,mId);

                                    if (quotesItemOldArray.size() > 0){
                                        long time1 = DateUtil.getStringToDate(quotesItemOldArray.get(quotesItemOldArray.size() - 1).getChildPriceDate(),"yyyy-MM-dd hh:mm:ss");
                                        long time2 = DateUtil.getStringToDate(queryQuotesAllList.get(0).getChildPriceDate(),"yyyy-MM-dd hh:mm:ss");
                                        if (time1 == time2){
                                            QuotesItem quotesItem = new QuotesItem();
                                            quotesItem.setData("");
                                            quotesItem.setMid(quotesItemOldArray.get(quotesItemOldArray.size() - 1).getMid());
                                            quotesItem.setCmd(getQuotationResponse.getCmd()+"");
                                            quotesItem.setChildPriceFrom(getQuotationResponse.getDataList().get(0).getPriceFrom());
                                            quotesItem.setChildPriceTo(getQuotationResponse.getDataList().get(0).getPriceTo());
                                            quotesItem.setTokenFrom(getQuotationResponse.getTokenFrom());
                                            quotesItem.setTokenTo(getQuotationResponse.getTokenTo());
                                            quotesItem.setAmount(getQuotationResponse.getAmount());
                                            quotesItem.setChildPriceDate(getQuotationResponse.getDataList().get(0).getPriceDate());
                                            quotesItem.setPriceFrom(getQuotationResponse.getDataList().get(0).getPriceFrom());
                                            quotesItem.setPrice_time(getQuotationResponse.getDataList().get(0).getPriceTime()+"");
                                            quotesItem.setDateUnit(getQuotationResponse.getDateUnit());
                                            quotesItem.setPriceTo(getQuotationResponse.getPriceTo());
                                            QuotesDataBase.updateByTokenPair(this.context,quotesItem,getQuotationResponse.getTokenFrom(),getQuotationResponse.getTokenTo(),SPUtils.getInstance().getString(Constants.SP.KEY.TIME_TYPE,"24H"),quotesItemOldArray.get(quotesItemOldArray.size() - 1).getChildPriceDate());
                                            LiveEventBus.get(LiveDataKey.RECEIVE_SOCKET_QUOTES_DATA).post("0");

                                        }else if (time1 < time2){
                                            QuotesDataBase.clearByTokenPairPoint(context,this.tokenFrom,this.tokenTo,SPUtils.getInstance().getString(Constants.SP.KEY.TIME_TYPE,"24H"),quotesFirstItem.getChildPriceDate());
                                            quotesItemOldArray = QuotesDataBase.queryQuotesListByCategoryName(this.context ,this.tokenFrom,this.tokenTo,SPUtils.getInstance().getString(Constants.SP.KEY.TIME_TYPE,"24H"));
                                            mId = quotesItemOldArray.size();
                                            queryQuotesAllList = RateQuotesUtils.updateQuotesCurrentList(getQuotationResponse,mId);
                                            QuotesDataBase.addUserAllQuotes(context,queryQuotesAllList);
                                            LiveEventBus.get(LiveDataKey.RECEIVE_SOCKET_QUOTES_DATA).post("0");
                                        }

                                    }
                                }

                            }

                        } else {
                            Log.i("===getQuotationResponse=="+"==getDataList="+getQuotationResponse.getDataList().size(),new Gson().toJson(getQuotationResponse.getDataList())+"==getDataList="+getQuotationResponse.getDataList().size());
                            if (getQuotationResponse.getDataList().size() > 0){
                                ArrayList<QuotesItem> quotesItem2List = RateQuotesUtils.addQuotesAllList(getQuotationResponse);
                                QuotesDataBase.clearByTokenPair(context,this.tokenFrom,this.tokenTo,SPUtils.getInstance().getString(Constants.SP.KEY.TIME_TYPE,"24H"));
                                QuotesDataBase.addUserAllQuotes(context,quotesItem2List);
                                LiveEventBus.get(LiveDataKey.RECEIVE_SOCKET_QUOTES_DATA).post("0");
                            }else {
                                SocketSender.sendGetQuotationRequest(this.tokenFrom,this.tokenTo,SPUtils.getInstance().getString(Constants.SP.KEY.TIME_TYPE,"24H"), "");
                            }




                        }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

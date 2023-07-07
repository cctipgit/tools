package com.hash.coinconvert.ui.activity;

import static com.hash.coinconvert.Constants.SP.DEFAULT.DEFAULT_CURRENCY_VALUE;
import static com.hash.coinconvert.Constants.SP.KEY.SWIFTH_STATUS;
import static com.hash.coinconvert.Constants.SP.KEY.TIME_TYPE;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.lifecycle.LifecycleOwner;

import com.duxl.baselib.utils.SPUtils;

import com.exchange2currency.ef.currencyconvert.grpc.Price;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.google.gson.Gson;
import com.hash.coinconvert.Constants;
import com.hash.coinconvert.R;
import com.hash.coinconvert.database.CurrencyDao;
import com.hash.coinconvert.database.QuotesDataBase;
import com.hash.coinconvert.databinding.ActivityRateDetailBinding;
import com.hash.coinconvert.entity.CurrencyInfo;
import com.hash.coinconvert.entity.KLineEntity;
import com.hash.coinconvert.entity.QuotesItem;
import com.hash.coinconvert.entity.QuotesItemChild;
import com.hash.coinconvert.http.SocketSender;
import com.hash.coinconvert.http.response.GetRateDetailResponseProcess;
import com.hash.coinconvert.livedatabus.LiveDataKey;
import com.hash.coinconvert.quotes.RateQuotesUtils;
//import com.hash.coinconvert.utils.Dispatch;
import com.hash.coinconvert.utils.DateUtil;
import com.hash.coinconvert.utils.Dispatch;
import com.hash.coinconvert.utils.FlagLoader;
//import com.hash.coinconvert.utils.QuoteDispatch;
import com.hash.coinconvert.widget.SimpleChartGestureListener;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.zhangke.websocket.SimpleListener;
import com.zhangke.websocket.SocketListener;
import com.zhangke.websocket.WebSocketHandler;
import com.zhangke.websocket.response.ErrorResponse;

import org.java_websocket.framing.Framedata;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class RateDetailActivity extends BaseActivity {
    List<String> currencyInfoList = new ArrayList<>();
    String rateTokenTo = "";
    String rateFrom = "";
    private int mDecimicalCryptocurrency = Constants.SP.DEFAULT.DECIMICAL_CRYPTOCURRENCY;
    private int mDecimicalLegalTender = Constants.SP.DEFAULT.DECIMICAL_LEGAL_TENDER;
    private static final int RIGHT_CLICK_TAG = 10;
    int state = 0;
    boolean isGestureStart = false;
    private ActivityRateDetailBinding mBinding;
    boolean changeIsSelect = false;
    boolean isAdd = false;
    private long mLastRefreshTime;

    private Runnable refreshUIRunnable = new Runnable() {
        @Override
        public void run() {
            if (isDestroyed() || isFinishing()) {
                return;
            }
            mLastRefreshTime = System.currentTimeMillis();
            loadChartData();
        }
    };
    @SuppressLint("LongLogTag")
    private void loadFirstChartData(){
        String currentCoinPair;
        String[] tempCoinPair;
        if (!TextUtils.isEmpty(mBinding.tvOriginCurrencyName.getText().toString())){
            currentCoinPair = mBinding.tvOriginCurrencyName.getText().toString()+"/"+mBinding.tvTargetCurrencyName.getText().toString();
            tempCoinPair = currentCoinPair.split("/");
        }else {
            currentCoinPair = SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_NAME);
            tempCoinPair = currentCoinPair.split("/");
        }
        ArrayList<QuotesItem> arrayList = QuotesDataBase.queryQuotesListByCategoryName(getContext(),tempCoinPair[0],tempCoinPair[1],SPUtils.getInstance().getString(TIME_TYPE,"24H"));
        if (arrayList.size() > 0){
            mBinding.loading.setVisibility(View.VISIBLE);
            LiveEventBus.get(LiveDataKey.TIME_LINE_STATUS).post(SPUtils.getInstance().getString(TIME_TYPE));
            try {
                testChart(arrayList,rateTokenTo,rateFrom);
                mBinding.loading.setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else {
            SocketSender.sendGetQuotationRequest(tempCoinPair[0], tempCoinPair[1],SPUtils.getInstance().getString(TIME_TYPE,"24H"),"");
//            getRequest(tempCoinPair[0],tempCoinPair[1],SPUtils.getInstance().getString(TIME_TYPE,"24H"));
        }
    }
    @SuppressLint("LongLogTag")
    private void loadChartData(){
        String currentCoinPair;
        String[] tempCoinPair;
        if (!TextUtils.isEmpty(mBinding.tvOriginCurrencyName.getText().toString())){
            currentCoinPair = mBinding.tvOriginCurrencyName.getText().toString()+"/"+mBinding.tvTargetCurrencyName.getText().toString();
            tempCoinPair = currentCoinPair.split("/");
        }else {
            currentCoinPair = SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_NAME);
            tempCoinPair = currentCoinPair.split("/");
        }
        ArrayList<QuotesItem> arrayList = QuotesDataBase.queryQuotesListByCategoryName(getContext(),tempCoinPair[0],tempCoinPair[1],SPUtils.getInstance().getString(TIME_TYPE,"24H"));
        if (arrayList.size() > 0){
            QuotesItem object = arrayList.get(0);
            rateTokenTo = object.getTokenTo();
            rateFrom = object.getTokenFrom();
            mBinding.loading.setVisibility(View.VISIBLE);
            LiveEventBus.get(LiveDataKey.TIME_LINE_STATUS).post(SPUtils.getInstance().getString(TIME_TYPE));
            try {
                testChart(arrayList,rateTokenTo,rateFrom);
                mBinding.loading.setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else {
            SocketSender.sendGetQuotationRequest(tempCoinPair[0], tempCoinPair[1],SPUtils.getInstance().getString(TIME_TYPE,"24H"),"");
//            getRequest(tempCoinPair[0],tempCoinPair[1],SPUtils.getInstance().getString(TIME_TYPE,"24H"));
        }

    }

    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mDecimicalCryptocurrency = SPUtils.getInstance().getInt(Constants.SP.KEY.DECIMICAL_CRYPTOCURRENCY, Constants.SP.DEFAULT.DECIMICAL_CRYPTOCURRENCY);
        mDecimicalLegalTender = SPUtils.getInstance().getInt(Constants.SP.KEY.DECIMICAL_LEGAL_TENDER, Constants.SP.DEFAULT.DECIMICAL_LEGAL_TENDER);
//        registerReceiver(receiver , RateQuotesUtils.Filter());
        WebSocketHandler.getDefault().addListener(socketListener);
    }
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_rate_detail;
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void initView(View v) {
        super.initView(v);
        mBinding = ActivityRateDetailBinding.bind(v);


        RateQuotesUtils.beforePageInfo(getIntent().getStringExtra("currencyInfo"));
        String coin = SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_NAME);
        String[] coinTemp = coin.split("/");
        if (TextUtils.isEmpty(coin)){
            if (!SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_TYPE).equals("digital")){
                currencyInfoList.add(coinTemp[1]);
            }
        }
        if (!SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE).equals("digital")){
            currencyInfoList.add(coinTemp[0]);
        }
        if (!TextUtils.isEmpty(coin)){
            loadChartData();
        }
        SPUtils.getInstance().put(TIME_TYPE,"24H");
        mBinding.loading.setVisibility(View.VISIBLE);
        test();
        LiveEventBus.get(LiveDataKey.RECEIVE_SOCKET_QUOTES_DATA).observe((LifecycleOwner) getContext(), o -> {
            loadFirstChartData();
        });

        mBinding.lineChart.setOnChartGestureListener(new SimpleChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                super.onChartGestureStart(me, lastPerformedGesture);
                isGestureStart = true;
                mBinding.tvCurrentValue.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                super.onChartGestureEnd(me, lastPerformedGesture);
                isGestureStart = false;
                mBinding.tvCurrentValue.setVisibility(View.VISIBLE);
            }
        });


        mBinding.rfChangeCoin.setOnClickListener(v1 -> {
            mBinding.loading.setVisibility(View.VISIBLE);
            String s9 = SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_NAME);
            String[] temp = s9.split("/");
            if (!changeIsSelect){
                changeIsSelect = true;
                state = 0;
            }else {
                changeIsSelect = false;
                state = 1;
            }
            switch (state){
                case 0:
                    changeIsSelect = true;
                    state = 0;
                    mBinding.ivTargetCurrencyFirst.setVisibility(View.VISIBLE);
                    mBinding.ivTargetCurrencySwitch.setVisibility(View.INVISIBLE);
                    mBinding.tvOriginCurrencyName.setText(temp[1]);
                    mBinding.tvTargetCurrencyName.setText(temp[0]);
                    LiveEventBus.get(LiveDataKey.TIME_LINE_STATUS).post(SPUtils.getInstance().getString(TIME_TYPE));
                    getRequest(mBinding.tvOriginCurrencyName.getText().toString(), mBinding.tvTargetCurrencyName.getText().toString(), SPUtils.getInstance().getString(TIME_TYPE,"24H"));
                    FlagLoader.load(mBinding.ivOriginCurrencyIcon,0,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_FIRST));
                    FlagLoader.load(mBinding.ivTargetCurrencyIcon,0,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_ICON));
                    if (currencyInfoList.size() == 0){
                        if (!SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_TYPE).equals("digital")){
                            if (coinTemp[1].equals( mBinding.tvOriginCurrencyName.getText().toString())){
                                if (SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_BACK_NAME).equals(coinTemp[1])){
                                   List<CurrencyInfo> list =  new CurrencyDao().getCurrencyAll();
                                   for (CurrencyInfo currencyInfo : list){
                                       if (currencyInfo.token.equals(mBinding.tvOriginCurrencyName.getText().toString())){
                                           if (currencyInfo.currencyType.equals("digital")){
                                               mBinding.tvOriginCurrencyCount.setText( RateQuotesUtils.currentcyNumber(SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE)));
                                           }else {
                                               mBinding.tvOriginCurrencyCount.setText( RateQuotesUtils.currentcyNumber(SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_TYPE)));

                                           }
                                       }

                                   }

                                }else {
                                    mBinding.tvOriginCurrencyCount.setText( RateQuotesUtils.currentcyNumber(SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_TYPE)));
                                }
                            }else {
                                mBinding.tvOriginCurrencyCount.setText( RateQuotesUtils.currentcyNumber("digital"));
                            }
                        }
                        if (!SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE).equals("digital")){
                            if (coinTemp[0].equals(mBinding.tvOriginCurrencyName.getText().toString())){
                                mBinding.tvOriginCurrencyCount.setText( RateQuotesUtils.currentcyNumber("digital"));

                            }else {
                                mBinding.tvOriginCurrencyCount.setText( RateQuotesUtils.currentcyNumber("digital"));

                            }
                        }
                    }else {
                        for (String tempStr : currencyInfoList){
                            if (temp[0].equals(tempStr)){
                                if (SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE).equals("digital") && "digital".equals(SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_TYPE))){
                                    mBinding.tvOriginCurrencyCount.setText( RateQuotesUtils.currentcyNumber("digital"));
                                }else {
                                    if (SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_BACK_NAME).equals(coinTemp[1])){
                                        if (SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE).equals("digital")){
                                            mBinding.tvOriginCurrencyCount.setText( RateQuotesUtils.currentcyNumber("digital"));
                                        }else{
                                            List<CurrencyInfo> list =  new CurrencyDao().getCurrencyAll();
                                            for (CurrencyInfo currencyInfo : list){
                                                if (currencyInfo.token.equals(mBinding.tvOriginCurrencyName.getText().toString())){
                                                    if (currencyInfo.currencyType.equals("digital")){
                                                        mBinding.tvOriginCurrencyCount.setText( RateQuotesUtils.currentcyNumber("digital"));

                                                    }
                                                    else {
                                                        mBinding.tvOriginCurrencyCount.setText( RateQuotesUtils.currentcyNumber(SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_TYPE)));

                                                    }
                                                }
                                            }

                                        }
                                    }else {
                                        if (SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_BACK).equals("digital")){
                                            mBinding.tvOriginCurrencyCount.setText( RateQuotesUtils.currentcyNumber("digital"));

                                        }else {
                                            mBinding.tvOriginCurrencyCount.setText( RateQuotesUtils.currentcyNumber(SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_TYPE)));

                                        }
                                    }
                                }
                            }else {
                                List<CurrencyInfo> list =  new CurrencyDao().getCurrencyAll();
                                for (CurrencyInfo currencyInfo : list){
                                    if (currencyInfo.token.equals(mBinding.tvOriginCurrencyName.getText().toString())){
                                        if (currencyInfo.currencyType.equals("digital")){
                                            if (SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_BACK).equals("digital")){
                                                mBinding.tvOriginCurrencyCount.setText( RateQuotesUtils.currentcyNumber("digital"));

                                            }else {
                                                mBinding.tvOriginCurrencyCount.setText( RateQuotesUtils.currentcyNumber(SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE)));

                                            }

                                        }else {
                                            mBinding.tvOriginCurrencyCount.setText( RateQuotesUtils.currentcyNumber(SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_TYPE)));
                                        }
                                    }
                                }
                            }
                        }
                    }

                    LiveEventBus.get(LiveDataKey.ORIGINCURRENCYCOUNTSTATUS).post(mBinding.tvOriginCurrencyCount.getText().toString());


                    break;
                case 1:
                    changeIsSelect = false;
                    state = 1;
                    mBinding.ivTargetCurrencySwitch.setVisibility(View.VISIBLE);
                    mBinding.ivTargetCurrencyFirst.setVisibility(View.INVISIBLE);
                    mBinding.tvOriginCurrencyName.setText(temp[0]);
                    mBinding.tvTargetCurrencyName.setText(temp[1]);
                    LiveEventBus.get(LiveDataKey.TIME_LINE_STATUS).post(SPUtils.getInstance().getString(TIME_TYPE));
                    getRequest(mBinding.tvOriginCurrencyName.getText().toString(), mBinding.tvTargetCurrencyName.getText().toString(), SPUtils.getInstance().getString(TIME_TYPE,"24H"));
                    FlagLoader.load(mBinding.ivOriginCurrencyIcon,0,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_ICON));
                    FlagLoader.load(mBinding.ivTargetCurrencyIcon,0,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_FIRST));
                    if (currencyInfoList.size() == 0){
                        if (!SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_TYPE).equals("digital")){
                            if (coinTemp[1].equals( mBinding.tvOriginCurrencyName.getText().toString())){
                                if (SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE).equals("digital")){
                                    mBinding.tvOriginCurrencyCount.setText( RateQuotesUtils.currentcyNumber("digital"));
                                }else {
                                    mBinding.tvOriginCurrencyCount.setText( RateQuotesUtils.currentcyNumber(SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_TYPE)));
                                }
                            }else {
                                mBinding.tvOriginCurrencyCount.setText( RateQuotesUtils.currentcyNumber("digital"));
                            }
                        }
                        if (!SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE).equals("digital")){
                            if (coinTemp[0].equals(mBinding.tvOriginCurrencyName.getText().toString())){
                                mBinding.tvOriginCurrencyCount.setText( RateQuotesUtils.currentcyNumber("digital"));
                            }else {
                                mBinding.tvOriginCurrencyCount.setText( RateQuotesUtils.currentcyNumber("digital"));
                            }
                        }
                    }else {
                        for (String tempStr : currencyInfoList){
                            if (temp[0].equals(tempStr)){
                                List<CurrencyInfo> list =  new CurrencyDao().getCurrencyAll();
                                for (CurrencyInfo currencyInfo : list){
                                    if (currencyInfo.token.equals(mBinding.tvTargetCurrencyName.getText().toString())){
                                        if (currencyInfo.currencyType.equals("digital")){
                                            mBinding.tvOriginCurrencyCount.setText( RateQuotesUtils.currentcyNumber(SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE)));

                                        }else {
                                            mBinding.tvOriginCurrencyCount.setText( RateQuotesUtils.currentcyNumber(SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_TYPE)));
                                        }
                                    }
                                }
                            }else {
                                List<CurrencyInfo> list =  new CurrencyDao().getCurrencyAll();
                                for (CurrencyInfo currencyInfo : list){
                                    if (currencyInfo.token.equals(mBinding.tvTargetCurrencyName.getText().toString())){
                                        if (currencyInfo.currencyType.equals("digital")){
                                            mBinding.tvOriginCurrencyCount.setText( RateQuotesUtils.currentcyNumber(SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE)));

                                        }else {
                                            mBinding.tvOriginCurrencyCount.setText( RateQuotesUtils.currentcyNumber(SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE)));
                                        }
                                    }
                                }
                            }
                        }
                    }

                    LiveEventBus.get(LiveDataKey.ORIGINCURRENCYCOUNTSTATUS).post(mBinding.tvOriginCurrencyCount.getText().toString());

                    break;
            }
        });
        mBinding.dateSwitchView.switchChange(SPUtils.getInstance().getInt(SWIFTH_STATUS,0));

        mBinding.dateSwitchView.setOnSwitchChangedListener(position -> {
            mBinding.loading.setVisibility(View.VISIBLE);
            switch (position){
                case 0:
                    SPUtils.getInstance().put(TIME_TYPE,"24H");
                    break;
                case 1:
                    SPUtils.getInstance().put(TIME_TYPE,"7D");
                    break;
                case 2:
                    SPUtils.getInstance().put(TIME_TYPE,"1M");
                    break;
                case 3:
                    SPUtils.getInstance().put(TIME_TYPE,"1Y");
                    break;
                case 4:
                    SPUtils.getInstance().put(TIME_TYPE,"3Y");
                    break;
            }
            getRequest(mBinding.tvOriginCurrencyName.getText().toString(), mBinding.tvTargetCurrencyName.getText().toString(),  SPUtils.getInstance().getString(TIME_TYPE,"24H"));
        });



        mBinding.ivTargetCurrencySwitch.setOnClickListener(view -> {
            state = 1;
            goActivity(QuoteCurrencyActivity.class,RIGHT_CLICK_TAG);
        });
        mBinding.ivTargetCurrencyFirst.setOnClickListener(view -> {
            state = 0;
            goActivity(QuoteCurrencyActivity.class,RIGHT_CLICK_TAG);
        });
        mBinding.tvOriginCurrencyName.setOnClickListener(v1 -> {
            state = 0;
            goActivity(QuoteCurrencyActivity.class,RIGHT_CLICK_TAG);
        });
        mBinding.tvTargetCurrencyName.setOnClickListener(v1 -> {
            state = 1;
            goActivity(QuoteCurrencyActivity.class,RIGHT_CLICK_TAG);

        });
    }

    private void test() {
        mBinding.loading.setVisibility(View.VISIBLE);
        try {

            String s = SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_NAME);
            String[] temp = s.split("/");
            mBinding.tvOriginCurrencyName.setText(temp[0]);
            mBinding.tvTargetCurrencyName.setText(temp[1]);
            mBinding.tvOriginCurrencyCount.setText(RateQuotesUtils.currentcyNumber(SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE)));

            FlagLoader.load(mBinding.ivOriginCurrencyIcon,0,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_ICON));
            FlagLoader.load(mBinding.ivTargetCurrencyIcon,0,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_FIRST));

            if (!TextUtils.isEmpty(mBinding.tvOriginCurrencyName.getText().toString()) && !TextUtils.isEmpty(mBinding.tvTargetCurrencyName.getText().toString())){
                SocketSender.sendGetQuotationRequest(mBinding.tvOriginCurrencyName.getText().toString(),  mBinding.tvTargetCurrencyName.getText().toString(),SPUtils.getInstance().getString(TIME_TYPE,"24H"),"");
//                getRequest(mBinding.tvOriginCurrencyName.getText().toString(),  mBinding.tvTargetCurrencyName.getText().toString(),SPUtils.getInstance().getString(TIME_TYPE,"24H"));
            }else {
                SocketSender.sendGetQuotationRequest(temp[0], temp[1],SPUtils.getInstance().getString(TIME_TYPE,"24H"),"");
//                getRequest(temp[0], temp[1],SPUtils.getInstance().getString(TIME_TYPE,"24H"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void getRequest(String tokenTo,String tokenFrom,String dateUnit) {
        Dispatch.I.postDelayed(new GetRequestRunnable(tokenTo,tokenFrom,dateUnit),0);
    }

    private static class GetRequestRunnable implements Runnable {
        String tokenTo;
        String tokenFrom;
        String dateUnit;
        public GetRequestRunnable(String tokenTo, String tokenFrom,String dateUnit) {
            this.tokenFrom = tokenFrom;
            this.tokenTo = tokenTo;
            this.dateUnit = dateUnit;
        }

        @Override
        public void run() {
            SocketSender.sendGetQuotationRequest(tokenTo,  tokenFrom, dateUnit, "");
        }
    }


    @SuppressLint({"NewApi", "LongLogTag", "SetTextI18n"})
    private void testChart(List<QuotesItem> datas, String tokenTo, String tokenFrom) throws JSONException {

        if (datas == null) { return; }
        if (datas.size() > 0){
            ArrayList<Entry> values = new ArrayList<>();
            ArrayList<KLineEntity> stringArrayList = new ArrayList<>();
            List<String> priceList = new ArrayList<>();
            for (QuotesItem item : datas){
                BigDecimal priceBigDecimal = RateQuotesUtils.currencyQuotesRateCalculate(mBinding.tvOriginCurrencyName.getText().toString(),tokenFrom,item.childPriceFrom,item.childPriceTo); //TODO 计算chart y轴价格函数
                KLineEntity lineEntity = new KLineEntity();
                lineEntity.setPrice(priceBigDecimal.toPlainString());
                lineEntity.setTime(item.getChildPriceDate());
                lineEntity.setPriceTime(item.getPrice_time());
                priceList.add(priceBigDecimal.toPlainString());
                stringArrayList.add(lineEntity);
            }
            for (int i= 0 ; i< stringArrayList.size(); i++){
                KLineEntity lineEntity2 = stringArrayList.get(i);
                values.add(new Entry(i, new BigDecimal(lineEntity2.getPrice()).floatValue()));
            }
            if (priceList.size() > 0){
                BigDecimal firstBg = new BigDecimal(priceList.get(0));
                QuotesItem lastItem = datas.get(datas.size() - 1);
                BigDecimal lastPrice = RateQuotesUtils.currencyQuotesRateCalculate(mBinding.tvOriginCurrencyName.getText().toString(),tokenFrom,lastItem.childPriceFrom,lastItem.childPriceTo); //TODO 计算chart y轴价格函数
                BigDecimal lastBg = lastPrice;
                BigDecimal resultBg = new BigDecimal(firstBg.subtract(lastBg).toString());
                BigDecimal max = priceList.stream().map(it ->new BigDecimal(it)).max(BigDecimal::compareTo).get();
                BigDecimal min = priceList.stream().map(it ->new BigDecimal(it)).min(BigDecimal::compareTo).get();
                BigDecimal size = new BigDecimal(priceList.size());
                int ivTargetCurrencyFirstVisibility = mBinding.ivTargetCurrencyFirst.getVisibility();
                SPUtils.getInstance().put(Constants.SP.KEY.ORIGINCURRENCYCOUNT,mBinding.tvOriginCurrencyCount.getText().toString());
                BigDecimal avg2 = RateQuotesUtils.calculationAvg(priceList.stream().map(it ->new BigDecimal(it)).reduce(BigDecimal.ZERO,BigDecimal::add).divide(size,12,RoundingMode.HALF_UP),SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE));
                if (ivTargetCurrencyFirstVisibility == 0){
                    LiveEventBus.get(LiveDataKey.ORIGINCURRENCYCOUNTSTATUS).observe((LifecycleOwner) getContext(), o -> {
                        if (mBinding.tvOriginCurrencyCount.getText().toString().equals(SPUtils.getInstance().getInt(Constants.SP.KEY.DEFAULT_CURRENCY_VALUE,DEFAULT_CURRENCY_VALUE)+"")){
                            BigDecimal lastPrice2 = RateQuotesUtils.currencyQuotesRateCalculate(mBinding.tvOriginCurrencyName.getText().toString(),tokenFrom,lastItem.childPriceFrom,lastItem.childPriceTo);
                            String currentValue1 =  RateQuotesUtils.calculationCurrentcyPrecision(lastPrice2.multiply(new BigDecimal(SPUtils.getInstance().getInt(Constants.SP.KEY.DEFAULT_CURRENCY_VALUE,DEFAULT_CURRENCY_VALUE))), SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE));
                            mBinding.tvCurrentValue.setText(RateQuotesUtils.formatCount(RateQuotesUtils.calculationCurrentcyPrecision(new BigDecimal(currentValue1),SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE)),true,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE),mDecimicalLegalTender,mDecimicalCryptocurrency));
                            mBinding.tvHighValue.setText(RateQuotesUtils.formatCount(RateQuotesUtils.calculationCurrentcyPrecision(max.multiply(new BigDecimal(SPUtils.getInstance().getInt(Constants.SP.KEY.DEFAULT_CURRENCY_VALUE,DEFAULT_CURRENCY_VALUE))),SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE)),true,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE),mDecimicalLegalTender,mDecimicalCryptocurrency));
                            mBinding.tvLowValue.setText(RateQuotesUtils.formatCount(RateQuotesUtils.calculationCurrentcyPrecision(min.multiply(new BigDecimal(SPUtils.getInstance().getInt(Constants.SP.KEY.DEFAULT_CURRENCY_VALUE,DEFAULT_CURRENCY_VALUE))),SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE)),true,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE),mDecimicalLegalTender,mDecimicalCryptocurrency));
                            mBinding.tvAverageValue.setText(RateQuotesUtils.formatCount(RateQuotesUtils.calculationCurrentcyPrecision(avg2.multiply(new BigDecimal(SPUtils.getInstance().getInt(Constants.SP.KEY.DEFAULT_CURRENCY_VALUE,DEFAULT_CURRENCY_VALUE))),SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE)),true,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE),mDecimicalLegalTender,mDecimicalCryptocurrency));
                        }else {
                            String formatCurrentValue = RateQuotesUtils.formatCount(RateQuotesUtils.calculationCurrentcyPrecision(lastBg,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE)),true,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE),mDecimicalLegalTender,mDecimicalCryptocurrency);
                            mBinding.tvCurrentValue.setText(formatCurrentValue);
                            mBinding.tvHighValue.setText(RateQuotesUtils.formatCount(RateQuotesUtils.calculationCurrentcyPrecision(max,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE)),true,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE),mDecimicalLegalTender,mDecimicalCryptocurrency));
                            mBinding.tvLowValue.setText(RateQuotesUtils.formatCount(RateQuotesUtils.calculationCurrentcyPrecision(min,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE)),true,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE),mDecimicalLegalTender,mDecimicalCryptocurrency));
                            mBinding.tvAverageValue.setText(RateQuotesUtils.formatCount(RateQuotesUtils.calculationCurrentcyPrecision(avg2,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE)),true,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE),mDecimicalLegalTender,mDecimicalCryptocurrency));
                        }
                    });
                }
                String currentValue =  RateQuotesUtils.calculationCurrentcyPrecision(lastBg, SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE));
                if (!TextUtils.isEmpty(currentValue)){
                    if (mBinding.tvOriginCurrencyCount.getText().toString().equals(SPUtils.getInstance().getInt(Constants.SP.KEY.DEFAULT_CURRENCY_VALUE,DEFAULT_CURRENCY_VALUE)+"")){
                        BigDecimal lastPrice2 = RateQuotesUtils.currencyQuotesRateCalculate(mBinding.tvOriginCurrencyName.getText().toString(),tokenFrom,lastItem.childPriceFrom,lastItem.childPriceTo); 
                        String currentValue1 =  RateQuotesUtils.calculationCurrentcyPrecision(lastPrice2.multiply(new BigDecimal(SPUtils.getInstance().getInt(Constants.SP.KEY.DEFAULT_CURRENCY_VALUE,DEFAULT_CURRENCY_VALUE))), SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE));
                        mBinding.tvCurrentValue.setText(RateQuotesUtils.formatCount(RateQuotesUtils.calculationCurrentcyPrecision(new BigDecimal(currentValue1),SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE)),true,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE),mDecimicalLegalTender,mDecimicalCryptocurrency));
                    }else {
                        String formatCurrentValue = RateQuotesUtils.formatCount(RateQuotesUtils.calculationCurrentcyPrecision(lastBg,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE)),true,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE),mDecimicalLegalTender,mDecimicalCryptocurrency);
                        mBinding.tvCurrentValue.setText(formatCurrentValue);
                    }
                }
                String lowValue = RateQuotesUtils.calculationCurrentcyPrecision(min,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE));
                if (!TextUtils.isEmpty(lowValue)){
                    if (mBinding.tvOriginCurrencyCount.getText().toString().equals(SPUtils.getInstance().getInt(Constants.SP.KEY.DEFAULT_CURRENCY_VALUE,DEFAULT_CURRENCY_VALUE)+"")){
                        String currentValue1 =  RateQuotesUtils.calculationCurrentcyPrecision(min.multiply(new BigDecimal(SPUtils.getInstance().getInt(Constants.SP.KEY.DEFAULT_CURRENCY_VALUE,DEFAULT_CURRENCY_VALUE))), SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE));
                        mBinding.tvLowValue.setText(RateQuotesUtils.formatCount(currentValue1,true,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE),mDecimicalLegalTender,mDecimicalCryptocurrency));
                    }else {
                        mBinding.tvLowValue.setText(RateQuotesUtils.formatCount(lowValue,true,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE),mDecimicalLegalTender,mDecimicalCryptocurrency));


                    }
                }
                String highValue = RateQuotesUtils.calculationCurrentcyPrecision(max,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE));
                if (!TextUtils.isEmpty(highValue)){
                    if (mBinding.tvOriginCurrencyCount.getText().toString().equals(SPUtils.getInstance().getInt(Constants.SP.KEY.DEFAULT_CURRENCY_VALUE,DEFAULT_CURRENCY_VALUE)+"")){
                        String currentValue1 =  RateQuotesUtils.calculationCurrentcyPrecision(max.multiply(new BigDecimal(SPUtils.getInstance().getInt(Constants.SP.KEY.DEFAULT_CURRENCY_VALUE,DEFAULT_CURRENCY_VALUE))), SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE));
                        mBinding.tvHighValue.setText(RateQuotesUtils.formatCount(currentValue1,true,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE),mDecimicalLegalTender,mDecimicalCryptocurrency));
                    }else {
                        mBinding.tvHighValue.setText(RateQuotesUtils.formatCount(highValue,true,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE),mDecimicalLegalTender,mDecimicalCryptocurrency));
                    }
                }

                String averageValue = RateQuotesUtils.calculationCurrentcyPrecision(avg2,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE));
                if (!TextUtils.isEmpty(averageValue)){
                    if (mBinding.tvOriginCurrencyCount.getText().toString().equals(SPUtils.getInstance().getInt(Constants.SP.KEY.DEFAULT_CURRENCY_VALUE,DEFAULT_CURRENCY_VALUE)+"")){
                        BigDecimal avgBigDecimal = priceList.stream().map(it ->new BigDecimal(it)).reduce(BigDecimal.ZERO,BigDecimal::add).divide(size,12,RoundingMode.HALF_UP);
                        String currentValue1 =  RateQuotesUtils.calculationCurrentcyPrecision(avgBigDecimal.multiply(new BigDecimal(SPUtils.getInstance().getInt(Constants.SP.KEY.DEFAULT_CURRENCY_VALUE,DEFAULT_CURRENCY_VALUE))), SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE));
                        mBinding.tvAverageValue.setText(RateQuotesUtils.formatCount(currentValue1,true,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE),mDecimicalLegalTender,mDecimicalCryptocurrency));
                    }else {
                        mBinding.tvAverageValue.setText(RateQuotesUtils.formatCount(averageValue,true,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE),mDecimicalLegalTender,mDecimicalCryptocurrency));
                    }
                }
                if (mBinding.tvOriginCurrencyCount.getText().toString().equals(SPUtils.getInstance().getInt(Constants.SP.KEY.DEFAULT_CURRENCY_VALUE,DEFAULT_CURRENCY_VALUE)+"")){
                    BigDecimal first = new BigDecimal(priceList.get(0));
                    BigDecimal last = new BigDecimal(priceList.get(priceList.size()-1));
                    if (new BigDecimal(priceList.get(priceList.size()-1)).subtract(new BigDecimal(priceList.get(0))).doubleValue() == 0.00){
                        SPUtils.getInstance().put(Constants.SP.KEY.ISCURRENT,"0");
                        mBinding.tvChangeValue.setText("100"+"%");
                    }else {
                        SPUtils.getInstance().put(Constants.SP.KEY.ISCURRENT,"1");
                        if ("0.000000000000".equals(last.subtract(first).divide(first,12,RoundingMode.HALF_UP).toPlainString())){
                            mBinding.tvChangeValue.setText("100 %");
                        }else {
                            mBinding.tvChangeValue.setText(new BigDecimal( RateQuotesUtils.calculationCurrentcyPrecision(last.subtract(first).divide(first,12,RoundingMode.HALF_UP).multiply(new BigDecimal(100)),SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE))).toPlainString()+"%");
                        }
                    }
                    Entry entry = values.get(values.size() - 1);
                    SPUtils.getInstance().put(Constants.SP.KEY.CHANGEVALUE,mBinding.tvChangeValue.getText().toString());
                    mBinding.lineChart.setData(values, stringArrayList, isGestureStart, RateQuotesUtils.calculationCurrentcyPrecision(resultBg.divide(firstBg,4,RoundingMode.HALF_UP),SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE)), isAdd,entry.getX(),entry.getY(), last.subtract(first).divide(first,12,RoundingMode.HALF_UP).multiply(new BigDecimal(100)).toPlainString());
                    mBinding.loading.setVisibility(View.GONE);
                }else {
                    SPUtils.getInstance().put(Constants.SP.KEY.ISCURRENT,"1");
                    //last_point- first_point / first_point
                    BigDecimal first = new BigDecimal(priceList.get(0));
                    BigDecimal last = new BigDecimal(priceList.get(priceList.size()-1));
                    if ("0.000000000000".equals(last.subtract(first).divide(first,12,RoundingMode.HALF_UP).toPlainString())){
                        mBinding.tvChangeValue.setText("100 %");
                    }else {
                        mBinding.tvChangeValue.setText(new BigDecimal( RateQuotesUtils.calculationCurrentcyPrecision(last.subtract(first).divide(first,12,RoundingMode.HALF_UP).multiply(new BigDecimal(100)),SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE))).toPlainString()+"%");
                    }
                    Entry entry = values.get(values.size() - 1);
                    SPUtils.getInstance().put(Constants.SP.KEY.CHANGEVALUE,mBinding.tvChangeValue.getText().toString());
                    mBinding.lineChart.setData(values, stringArrayList, isGestureStart, RateQuotesUtils.calculationCurrentcyPrecision(resultBg.divide(firstBg,4,RoundingMode.HALF_UP),SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE)), isAdd,entry.getX(),entry.getY(), last.subtract(first).divide(first,12,RoundingMode.HALF_UP).multiply(new BigDecimal(100)).toPlainString());
                    mBinding.loading.setVisibility(View.GONE);
                }
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        slideOutRight();
    }

    @Override
    public void finish() {
        super.finish();
        slideOutRight();
    }

    private SocketListener socketListener = new SimpleListener() {
        @Override
        public void onConnected() {
            super.onConnected();
            String currentCoinPair;
            String[] tempCoinPair;
            if (!TextUtils.isEmpty(mBinding.tvOriginCurrencyName.getText().toString())){
                currentCoinPair = mBinding.tvOriginCurrencyName.getText().toString()+"/"+mBinding.tvTargetCurrencyName.getText().toString();
                tempCoinPair = currentCoinPair.split("/");
            }else {
                currentCoinPair = SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_NAME);
                tempCoinPair = currentCoinPair.split("/");
            }
            getRequest(tempCoinPair[0],  tempCoinPair[1],SPUtils.getInstance().getString(TIME_TYPE,"24h"));
        }

        @Override
        public void onConnectFailed(Throwable e) {
            super.onConnectFailed(e);
        }

        @Override
        public void onDisconnect() {
            super.onDisconnect();
            Dispatch.I.postDelayed(() -> {
                if (!WebSocketHandler.getDefault().isConnect()) {
                    WebSocketHandler.getDefault().reconnect();
                }
            }, 1000);

        }

        @Override
        public <T> void onMessage(String message, T data) {
            super.onMessage(message, data);
        }

        @Override
        public void onSendDataError(ErrorResponse errorResponse) {
            super.onSendDataError(errorResponse);
        }

        @SuppressLint("LongLogTag")
        @Override
        public <T> void onMessage(ByteBuffer bytes, T data) {

            super.onMessage(bytes, data);
            Dispatch.I.postDelayed(GetRateDetailResponseProcess.process(bytes, getContext()),0);
        }
        @Override
        public void onPing(Framedata framedata) {
            super.onPing(framedata);
            Log.d("send","ping");
        }

        @Override
        public void onPong(Framedata framedata) {
            super.onPong(framedata);
            Log.d("send","pong");

        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SocketSender.sendGetQuotationStopRequest();
        WebSocketHandler.getDefault().removeListener(socketListener);
        Dispatch.I.removeUICallbacks(refreshUIRunnable);
    }
    @Override
    protected void onActivityResult(int requestCode, ActivityResult result) {
        super.onActivityResult(requestCode, result);
        if (requestCode == 10){
            mBinding.loading.setVisibility(View.VISIBLE);
            String s = SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_NAME);
            String[] temp = s.split("/");
            CurrencyInfo currencyInfo = result.getData().getParcelableExtra("data");
            try {
                if (!currencyInfo.currencyType.equals("digital")){
                    currencyInfoList.add(currencyInfo.token);
                    SPUtils.getInstance().put(Constants.SP.KEY.CURRENT_NAME,temp[0]+"/"+currencyInfo.token);
                }
                SPUtils.getInstance().put(Constants.SP.KEY.CURRENT_NAME,temp[0]+"/"+currencyInfo.token);
                SPUtils.getInstance().put(Constants.SP.KEY.CURRENT_BACK,currencyInfo.currencyType);
                SPUtils.getInstance().put(Constants.SP.KEY.CURRENT_BACK_NAME,currencyInfo.token);
                SPUtils.getInstance().put(Constants.SP.KEY.CURRENT_FIRST, currencyInfo.icon);

                String s1 = SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_NAME);
                String[] tempArr = s1.split("/");
                if (state == 0){

                    FlagLoader.load(mBinding.ivOriginCurrencyIcon,0,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_FIRST));
                    mBinding.tvOriginCurrencyName.setText(tempArr[1]);
                    if ("digital".equals(SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_BACK))){
                        mBinding.tvOriginCurrencyCount.setText("1");
                    }else {
                        mBinding.tvOriginCurrencyCount.setText(SPUtils.getInstance().getInt(Constants.SP.KEY.DEFAULT_CURRENCY_VALUE,DEFAULT_CURRENCY_VALUE)+"");
                    }
                }else {
                    FlagLoader.load(mBinding.ivTargetCurrencyIcon,0,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_FIRST));
                    mBinding.tvTargetCurrencyName.setText(tempArr[1]);
                }
                getRequest(mBinding.tvOriginCurrencyName.getText().toString(), mBinding.tvTargetCurrencyName.getText().toString(), SPUtils.getInstance().getString(TIME_TYPE,"24H"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

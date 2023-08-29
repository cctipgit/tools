package com.rate.quiz.widget;

import static com.rate.quiz.Constants.SP.DEFAULT.DEFAULT_CURRENCY_VALUE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.duxl.baselib.utils.DisplayUtil;
import com.duxl.baselib.utils.SPUtils;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.rate.quiz.Constants;
import com.rate.quiz.R;
import com.rate.quiz.databinding.LayoutLineChartViewBinding;
import com.rate.quiz.entity.KLineEntity;
import com.rate.quiz.livedatabus.LiveDataKey;
import com.rate.quiz.quotes.RateQuotesUtils;
import com.rate.quiz.utils.AnimUtils;
import com.rate.quiz.utils.DateUtil;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class LineChartView extends FrameLayout implements ChartMarkerView.OnMarketOffsetListener {
    private  ArrayList<Entry> values = new ArrayList<>();
    private  LineDataSet set1 = new LineDataSet(values, "DataSet 1");;

    private LayoutLineChartViewBinding mBinding;
    private int mPadding;

    public LineChartView(@NonNull Context context) {
        this(context, null);
    }

    public LineChartView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_line_chart_view, this, false);
        mBinding = LayoutLineChartViewBinding.bind(view);
        addView(view);
        mPadding = DisplayUtil.dip2px(context, 16);
        LiveEventBus.get(LiveDataKey.TIME_LINE_STATUS).observe((LifecycleOwner) getContext(), o -> updateUiTime());
        initChart(mBinding.chart);


    }

    private void updateUiTime(){

        mBinding.rlIncome.setFilterTouchesWhenObscured(true);
        if (SPUtils.getInstance().getString(Constants.SP.KEY.TIME_TYPE).equals("24H")){
            mBinding.rlIncome.setVisibility(View.VISIBLE);
        }else {
            mBinding.rlIncome.setVisibility(View.INVISIBLE);
        }

        if (!TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.SP.KEY.CHANGEVALUE))){
            if(SPUtils.getInstance().getInt(Constants.SP.KEY.QUOTE_COLOR,Constants.SP.DEFAULT.QUOTE_COLOR) == 0){
                if (!SPUtils.getInstance().getString(Constants.SP.KEY.CHANGEVALUE).contains("-")){
                    setChartlineColor(set1,0);
                    setChartBg(set1, ContextCompat.getDrawable(getContext(), R.drawable.fade_red));
                }else {
                    setChartlineColor(set1,1);
                    setChartBg(set1, ContextCompat.getDrawable(getContext(), R.drawable.fade_green));
                }
            }else {
                if (!SPUtils.getInstance().getString(Constants.SP.KEY.CHANGEVALUE).contains("-")){

                    setChartlineColor(set1,1);
                    setChartBg(set1, ContextCompat.getDrawable(getContext(), R.drawable.fade_green));

                }else {
                    setChartlineColor(set1,0);
                    setChartBg(set1, ContextCompat.getDrawable(getContext(), R.drawable.fade_red));

                }
            }
        }



    }
    private void setChartlineColor(LineDataSet set1,int redId){
        if (redId == 0){
            set1.setColor(getResources().getColor(R.color.chart_red_h_border));
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            mBinding.chart.setData(data);
            mBinding.chart.getData().notifyDataChanged();
            mBinding.chart.notifyDataSetChanged();
        }else {
            set1.setColor(getResources().getColor(R.color.chart_green_h_border));
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            mBinding.chart.setData(data);
            mBinding.chart.getData().notifyDataChanged();
            mBinding.chart.notifyDataSetChanged();
        }
    }
    private void setChartBg(LineDataSet set1,Drawable drawable){
        set1.setFillDrawable(drawable);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        LineData data = new LineData(dataSets);
        mBinding.chart.setData(data);
        mBinding.chart.getData().notifyDataChanged();
        mBinding.chart.notifyDataSetChanged();

    }

    private void initChart(LineChart lineChart) {

        lineChart.setViewPortOffsets(mPadding, 0, mPadding, 0);
        lineChart.getDescription().setEnabled(false);
        lineChart.setNoDataText("");
        lineChart.setAutoScaleMinMaxEnabled(true);
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (mGestureListener != null) {
                    mGestureListener.onChartGestureStart(null, null);
                }
                mBinding.llTopMarket.setVisibility(View.VISIBLE);
                mBinding.tvAnimMarket.setVisibility(View.VISIBLE);
                AnimUtils.startAnimation(mBinding.tvAnimMarket, R.anim.market_point);

                if (kData.size() >= 0 && dataEntity.size() > 0){
                    Entry entry = dataEntity.get((int) h.getX());
                    KLineEntity kLineEntity = kData.get((int) entry.getX());
                    if (SPUtils.getInstance().getString(Constants.SP.KEY.ORIGINCURRENCYCOUNT).equals("1")){
                        mBinding.priceTv.setText(RateQuotesUtils.calculationCurrentcyPrecision(new BigDecimal(kLineEntity.getPrice()).setScale(12,RoundingMode.HALF_UP),SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE)));
                    }else if (SPUtils.getInstance().getString(Constants.SP.KEY.ORIGINCURRENCYCOUNT).equals(SPUtils.getInstance().getInt(Constants.SP.KEY.DEFAULT_CURRENCY_VALUE,DEFAULT_CURRENCY_VALUE)) || !SPUtils.getInstance().getString(Constants.SP.KEY.ORIGINCURRENCYCOUNT).equals("1")){
                        mBinding.priceTv.setText(RateQuotesUtils.calculationCurrentcyPrecision(new BigDecimal(kLineEntity.getPrice()).setScale(12, RoundingMode.HALF_UP).multiply(new BigDecimal(SPUtils.getInstance().getInt(Constants.SP.KEY.DEFAULT_CURRENCY_VALUE,DEFAULT_CURRENCY_VALUE))),SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE)));
                    }
                    Locale locale = getResources().getConfiguration().locale;
                    if (locale.getLanguage().contains("zh")||locale.getLanguage().contains("cn")){
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            Date date = simpleDateFormat.parse(DateUtil.stampToDate(kLineEntity.getPriceTime()));
                            SimpleDateFormat dateFormat = new SimpleDateFormat("M月d日 HH:mm");
                            String getTime = dateFormat.format(date);
                            mBinding.timeTv.setText(getTime);
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }

                    }else {
                        mBinding.timeTv.setText(DateUtil.getTimeFormat(getContext(), kLineEntity.getTime()));
                    }
                }

            }

            @Override
            public void onNothingSelected() {
            }
        });
        lineChart.setOnChartGestureListener(new SimpleChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                onMarketOffset(me.getX(), 0);
            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

                lineChart.highlightValues(null);
                if (mGestureListener != null) {
                    mGestureListener.onChartGestureEnd(me, lastPerformedGesture);
                }
                mBinding.llTopMarket.setVisibility(View.INVISIBLE);
                mBinding.tvAnimMarket.clearAnimation();
                mBinding.tvAnimMarket.setVisibility(View.INVISIBLE);

            }
        });
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getXAxis().setEnabled(false);

        lineChart.setDrawGridBackground(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setScaleXEnabled(false);
        lineChart.setScaleYEnabled(false);
        lineChart.animateX(800, Easing.EaseInQuart);
        ChartMarkerView markerView = new ChartMarkerView(getContext());
        markerView.setOnMarketOffsetListener(this);
        markerView.setChartView(lineChart);
        lineChart.setMarker(markerView);
        lineChart.setHighlightPerTapEnabled(false);
        lineChart.setHighlightPerDragEnabled(true);

        values = new ArrayList<>();
        set1 = new LineDataSet(values, "DataSet 1");

        set1.setDrawIcons(false);

        // draw dashed line
        set1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        set1.setDrawValues(false);

        if (!TextUtils.isEmpty(mBinding.tvIncome.getText().toString())){
            if(SPUtils.getInstance().getInt(Constants.SP.KEY.QUOTE_COLOR,Constants.SP.DEFAULT.QUOTE_COLOR) == 0){
                if (!mBinding.tvIncome.getText().toString().contains("-")){
                    setChartlineColor(set1,0);
                }else {
                    setChartlineColor(set1,1);
                }
            }else {
                if (!mBinding.tvIncome.getText().toString().contains("-")){

                    setChartlineColor(set1,1);

                }else {
                    setChartlineColor(set1,0);

                }
            }
        }

        set1.setCircleColor(Color.TRANSPARENT);
        // line thickness and point size
        set1.setLineWidth(2f);
        set1.setCircleRadius(3f);
        // draw points as solid circles
        set1.setDrawCircleHole(false);

        // customize legend entry
        set1.setFormLineWidth(1f);
        set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 1f}, 0f));
        set1.setFormSize(15.f);
        set1.setHighLightColor(getResources().getColor(R.color.chart_select_line));
        set1.enableDashedHighlightLine(20f, 0f, 0f);

        set1.setHighlightLineWidth(DisplayUtil.dip2px(getContext(), .5f));
        set1.setHighlightEnabled(true);
        set1.setDrawHighlightIndicators(true);
        set1.setDrawHorizontalHighlightIndicator(false);

        // text size of values
        set1.setValueTextSize(9f);

        // set the filled area
        set1.setDrawFilled(true);
        set1.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return lineChart.getAxisLeft().getAxisMinimum();
            }
        });
//        Drawable drawable1 = null;

        if (!TextUtils.isEmpty(mBinding.tvIncome.getText().toString())){
            if(SPUtils.getInstance().getInt(Constants.SP.KEY.QUOTE_COLOR,Constants.SP.DEFAULT.QUOTE_COLOR) == 0){
                if (!mBinding.tvIncome.getText().toString().contains("-")){
                    setChartBg(set1, ContextCompat.getDrawable(getContext(), R.drawable.fade_red));
                }else {
                    setChartBg(set1, ContextCompat.getDrawable(getContext(), R.drawable.fade_green));
                }
            }else {
                if (!mBinding.tvIncome.getText().toString().contains("-")){

                    setChartBg(set1, ContextCompat.getDrawable(getContext(), R.drawable.fade_green));

                }else {
                    setChartBg(set1, ContextCompat.getDrawable(getContext(), R.drawable.fade_red));

                }
            }
        }

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the data sets

        // create a data object with the data sets
        LineData data = new LineData(dataSets);

        // set data
        lineChart.setData(data);
    }
    ArrayList<KLineEntity> kData = new ArrayList<>();
    List<Entry> dataEntity = new ArrayList<>();
    boolean isGestureStart = false;
    float x1 = 0;
    @SuppressLint("Range")
    public void setData(List<Entry> data, ArrayList<KLineEntity> kLinList,
                        boolean isGestureStart1, String avg, boolean isAddSub,
                        float X, float Y,String changeValue) {
        this.isGestureStart = isGestureStart1;

        if (mBinding.chart.getData() != null &&
                mBinding.chart.getData().getDataSetCount() > 0) {
            this.x1 = X;
            if(SPUtils.getInstance().getInt(Constants.SP.KEY.QUOTE_COLOR,Constants.SP.DEFAULT.QUOTE_COLOR) == 0){
                if (!changeValue.contains("-")){
                    setChartlineColor(set1,0);
                    setChartBg(set1, ContextCompat.getDrawable(getContext(), R.drawable.fade_red));
                    mBinding.tvIncome.setTextColor(getResources().getColor(R.color.chart_red_h_border));
                }else {
                    setChartlineColor(set1,1);
                    setChartBg(set1, ContextCompat.getDrawable(getContext(), R.drawable.fade_green));
                    mBinding.tvIncome.setTextColor(getResources().getColor(R.color.chart_green_h_border));
                }
            }else {
                if (!changeValue.contains("-")){

                    setChartlineColor(set1,1);
                    setChartBg(set1, ContextCompat.getDrawable(getContext(), R.drawable.fade_green));
                    mBinding.tvIncome.setTextColor(getResources().getColor(R.color.chart_green_h_border));

                }else {
                    setChartlineColor(set1,0);
                    setChartBg(set1, ContextCompat.getDrawable(getContext(), R.drawable.fade_red));
                    mBinding.tvIncome.setTextColor(getResources().getColor(R.color.chart_red_h_border));

                }
            }
            this.kData = kLinList;
            this.dataEntity = data;

            if (!SPUtils.getInstance().getString(Constants.SP.KEY.ISCURRENT).equals("0")){
                if ("0.000000000000".equals(changeValue)){
                    mBinding.tvIncome.setText("100 %");
                }else {
                    mBinding.tvIncome.setText(""+new BigDecimal(RateQuotesUtils.calculationCurrentcyPrecision(new BigDecimal(changeValue),SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_SLIDE_TYPE))).toPlainString()+"%");
                }
            }else {
                mBinding.tvIncome.setText("100 %");
            }

            LineDataSet set1 = (LineDataSet) mBinding.chart.getData().getDataSetByIndex(0);
            set1.setValues(data);
            set1.notifyDataSetChanged();


            FrameLayout.LayoutParams layoutParamsChart = (LayoutParams) mBinding.rlIncome.getLayoutParams();
            if (kData.size() >= 0 && dataEntity.size() > 0){
                Entry entry = dataEntity.get((int) dataEntity.size() - 1);
                MPPointF offset = mBinding.chart.getPosition(entry, YAxis.AxisDependency.RIGHT);
                int tvAnimMarketWidth = mBinding.tvAnimMarket.getWidth();
                int y = (int)(offset.y - tvAnimMarketWidth  / 2f);

                if (y >= 400 || y >= 399 || y <= 399 || y <= 411){
                    if ( y == 0){
                        layoutParamsChart.setMargins(0,(int)(offset.y - tvAnimMarketWidth  / 2f) + 20,0,0);
                    }else {
                        int index = (int)(offset.y - tvAnimMarketWidth  / 2f) - 25;
                         if (index < 0) {
                            layoutParamsChart.setMargins(0,(int)(offset.y - tvAnimMarketWidth  / 2f) - 20,0,0);
                        }else {
                            layoutParamsChart.setMargins(0,(int)(offset.y - tvAnimMarketWidth  / 2f) - 30,0,0);
                        }
                    }
                }else {
                    layoutParamsChart.setMargins(0,(int)(offset.y - tvAnimMarketWidth  / 2f),0,0);
                }
                Log.i("==yVal===",(int)(offset.y - tvAnimMarketWidth  / 2f)+"");
                mBinding.rlIncome.setLayoutParams(layoutParamsChart);
            }

            mBinding.chart.getData().notifyDataChanged();
            mBinding.chart.notifyDataSetChanged();

        }

    }


    private OnChartGestureListener mGestureListener;

    public void setOnChartGestureListener(OnChartGestureListener l) {
        this.mGestureListener = l;
    }

    @Override
    public void onMarketOffset(float posX, float posY) {

        Log.i("duxl.log", "onMarketOffset: posX=" + posX + ", posY=" + posY + ", chartWidth=" + mBinding.chart.getWidth());
        int llValueWidth = mBinding.llTopMarket.getWidth();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mBinding.llTopMarket.getLayoutParams();
        layoutParams.leftMargin = mPadding;
        if (llValueWidth / 2 + mPadding < posX) {
            layoutParams.leftMargin += posX - llValueWidth / 2 - mPadding;
        }
        if (layoutParams.leftMargin > mBinding.chart.getWidth() - mPadding - llValueWidth) {
            layoutParams.leftMargin = mBinding.chart.getWidth() - mPadding - llValueWidth;
        }
        mBinding.llTopMarket.setLayoutParams(layoutParams);
//
        int tvAnimMarketWidth = mBinding.tvAnimMarket.getWidth();
        FrameLayout.LayoutParams layoutParams2 = (LayoutParams) mBinding.tvAnimMarket.getLayoutParams();
//        Entry entry = dataEntity.get((int) dataEntity.size() - 1);
//        MPPointF offset = mBinding.chart.getPosition(entry, YAxis.AxisDependency.RIGHT);
        layoutParams2.leftMargin = (int)(posX - tvAnimMarketWidth / 2f);
        layoutParams2.topMargin = (int)(posY - tvAnimMarketWidth  / 2f);
        mBinding.tvAnimMarket.setLayoutParams(layoutParams2);




    }
}


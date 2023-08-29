package com.rate.quiz.widget;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.rate.quiz.R;

public class ChartMarkerView extends MarkerView {

    private final TextView tvPoint;

    public ChartMarkerView(Context context) {
        super(context, R.layout.layout_chart_marker);

        tvPoint = findViewById(R.id.tv_point);
    }

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        super.refreshContent(e, highlight);
        //Log.i("duxl.log", "refreshContent");
        /*if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;

            tvContent.setText(Utils.formatNumber(ce.getHigh(), 0, true));
        } else {

            tvContent.setText(Utils.formatNumber(e.getY(), 0, true));
        }*/

    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight() / 2);
    }

    @Override
    public MPPointF getOffsetForDrawingAtPoint(float posX, float posY) {
        MPPointF mpPointF = super.getOffsetForDrawingAtPoint(posX, posY);
        if (mOnMarketOffsetListener != null) {
            mOnMarketOffsetListener.onMarketOffset(posX, posY);
        }
        return mpPointF;
    }

    public interface OnMarketOffsetListener {
        void onMarketOffset(float posX, float posY);
    }

    private OnMarketOffsetListener mOnMarketOffsetListener;

    public void setOnMarketOffsetListener(OnMarketOffsetListener l) {
        this.mOnMarketOffsetListener = l;
    }
}

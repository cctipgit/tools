package com.rate.quiz.entity;

import com.google.gson.annotations.SerializedName;
import com.rate.quiz.widget.KLineView;

public class ChartItem implements KLineView.KLinePoint {
    /**
     * close price
     */
    @SerializedName("price")
    public float c;
    /**
     * timestamp
     */
    @SerializedName("ts")
    public long t;

    @Override
    public long getTimestamp() {
        return t;
    }

    @Override
    public float getPrice() {
        return c;
    }
}

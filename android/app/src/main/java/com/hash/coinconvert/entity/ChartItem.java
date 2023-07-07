package com.hash.coinconvert.entity;

import com.hash.coinconvert.widget.KLineView;

public class ChartItem implements KLineView.KLinePoint {
    /**
     * close price
     */
    public float c;
    /**
     * timestamp
     */
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

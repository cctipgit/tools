package com.hash.coinconvert.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.duxl.baselib.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

public class KLineView extends View {

    private Paint paint;

    private int lineColor;
    private int lineWidth;
    private int gradientStartColor;

    private Path gradientPath;

    private Shader gradient;

    private List<KLinePoint> data = new ArrayList<>();
    private float minY;
    private float maxY;

    private OnStatListener onStatListener;

    public KLineView(Context context) {
        super(context);
        init(context);
    }

    public KLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public KLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
        lineColor = Color.parseColor("#8458F3");
        lineWidth = DisplayUtil.dip2px(context, 2f);
        gradientStartColor = Color.parseColor("#778458F3");
        //#7747F299, #7747F275, #1E065B00

        gradientPath = new Path();
        paint.setColor(lineColor);
        paint.setStrokeWidth(lineWidth);
        paint.setAntiAlias(true);
        paint.setStrokeJoin(Paint.Join.ROUND);

        if (isInEditMode()) {
            long t = System.currentTimeMillis();
            float seed = (float) (Math.random() * 100f);
            List<KLinePoint> list = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                boolean sig = Math.random() < 0.5;
                float change = (float) (Math.random() * 5f) * (sig ? -1 : 1);
                list.add(new KLinePointImp(t + i * 100, seed += change));
            }
            setData(list);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        Log.d("MeasureSpec", "width:" + width);
    }

    public void setData(List<? extends KLinePoint> data) {
        this.data.clear();
        this.data.addAll(data);
        calculateData();
        invalidate();
    }

    private float lastPointX;
    private float lastPointY;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        gradientPath.reset();
        int width = getWidth();
        int height = getHeight();
        gradientPath.moveTo(0, height);
        paint.setShader(null);
        paint.setStyle(Paint.Style.STROKE);
        for (int i = 0; i < data.size(); i++) {
            KLinePoint point = data.get(i);
            float x = calculateX(point.getTimestamp());
            float y = calculateY(point.getPrice());
            gradientPath.lineTo(x, y);
            if (i > 0) {
                canvas.drawLine(lastPointX, lastPointY, x, y, paint);
            }
            lastPointX = x;
            lastPointY = y;
        }
        gradientPath.lineTo(width, height);
        gradientPath.close();
        if (gradient == null) {
            gradient = new LinearGradient(0, 0, 0, height, new int[]{gradientStartColor, Color.TRANSPARENT}, null, Shader.TileMode.CLAMP);
        }
        paint.setShader(gradient);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(gradientPath, paint);
    }

    public float calculateX(long timestamp) {
        int len = data.size();
        if (len > 0) {
            long startTime = data.get(0).getTimestamp();
            long totalTime = data.get(len - 1).getTimestamp() - startTime;
            int width = getMeasuredWidth();
            return (timestamp - startTime) * 1f / totalTime * width;
        }
        return 0f;
    }

    public float calculateY(float value) {
        float paddingTop = getPaddingTop();
        float paddingBottom = getPaddingBottom();
        float total = maxY - minY;
        float height = getMeasuredHeight();
        float availableHeight = height - paddingBottom - paddingTop;
        if (total == 0f) {
            return availableHeight/2 - paddingBottom;
        }
        float dif = (value - minY) / total * availableHeight;
        return height - paddingBottom - dif;
    }

    public void calculateData() {
        minY = Float.MAX_VALUE;
        maxY = Float.MIN_VALUE;

        float sum = 0f;
        for (KLinePoint item : data) {
            minY = Math.min(minY, item.getPrice());
            maxY = Math.max(maxY, item.getPrice());
            sum += item.getPrice();
        }
        if (onStatListener != null && !data.isEmpty()) {
            onStatListener.onStat(maxY, minY, sum / data.size(), data.get(data.size() - 1).getPrice() - data.get(0).getPrice());
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.onStatListener = null;
    }

    public void setOnStatListener(OnStatListener onStatListener) {
        this.onStatListener = onStatListener;
    }

    public interface KLinePoint {
        long getTimestamp();

        float getPrice();
    }

    public interface OnStatListener {
        void onStat(float high, float low, float avg, float change);
    }

    public static class KLinePointImp implements KLinePoint {

        private long timestamp;
        private float price;

        public KLinePointImp(long timestamp, float price) {
            this.timestamp = timestamp;
            this.price = price;
        }

        @Override
        public long getTimestamp() {
            return timestamp;
        }

        @Override
        public float getPrice() {
            return price;
        }
    }
}

package com.hash.coinconvert.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.hash.coinconvert.R;

/**
 * 不支持换行
 */
public class StrokeTextView extends androidx.appcompat.widget.AppCompatTextView {

    public StrokeTextView(Context context) {
        this(context, null);
    }

    public StrokeTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StrokeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs, defStyleAttr);
    }

    private int strokeColor;
    private float strokeWidth;
    private TextView strokeTextView;

    private void initAttr(Context context, AttributeSet attributeSet, int defStyleAttr) {
        TypedArray ta = context.obtainStyledAttributes(attributeSet, R.styleable.StrokeTextView, defStyleAttr, R.style.StrokeTextViewStyle);
        strokeColor = ta.getColor(R.styleable.StrokeTextView_strokeColor, Color.WHITE);
        strokeWidth = ta.getDimension(R.styleable.StrokeTextView_strokeWidth, 0f);
        ta.recycle();
        strokeTextView = new TextView(context, attributeSet, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        strokeTextView.setTypeface(getTypeface());
        strokeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextSize());
        strokeTextView.setLayoutParams(getLayoutParams());
        Paint strokePaint = strokeTextView.getPaint();
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(strokeColor);
        strokePaint.setStrokeWidth(strokeWidth);
        strokeTextView.setTextColor(strokeColor);
        strokeTextView.setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
        strokeTextView.setText(getText());
        strokeTextView.draw(canvas);
        canvas.restore();
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        strokeTextView.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        strokeTextView.layout(left, top, right, bottom);
    }
}

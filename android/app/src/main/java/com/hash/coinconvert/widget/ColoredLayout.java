package com.hash.coinconvert.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.duxl.baselib.utils.DisplayUtil;
import com.duxl.baselib.utils.SPUtils;
import com.hash.coinconvert.Constants;
import com.hash.coinconvert.R;

public class ColoredLayout extends LinearLayout {

    private int startColor;
    private int endColor;

    private int borderRadius;
    private Paint paint;
    private Path clipPath;
    private Rect rect;

    /**
     * negative means down
     * positive means up
     * zero means nothing
     */
    private float state;
    private float price;
    private LinearGradient lgRed;
    private LinearGradient lgGreen;

    private int bg;

    private ValueAnimator valueAnimator;

    private float animationValue;

    public ColoredLayout(Context context) {
        super(context);
        init(context);
    }

    public ColoredLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ColoredLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
        paint.setAntiAlias(true);
        clipPath = new Path();
        rect = new Rect();
        borderRadius = DisplayUtil.dip2px(context, 16f);
        valueAnimator = ObjectAnimator.ofFloat(0).setDuration(1000);
        valueAnimator.addUpdateListener(animation -> {
            animationValue = (float) animation.getAnimatedValue();
            invalidate();
        });
        bg = ContextCompat.getColor(context, R.color.theme_bg);
    }

    public void setPrice(float price) {
        float state;
        if (this.price == 0) {
            state = 0;
        } else {
            state = price - this.price;
        }
        this.price = price;
        if (state * this.state > 0) {
            return;
        }
        this.state = state;

        int quoteColorType = SPUtils.getInstance().getInt(Constants.SP.KEY.QUOTE_COLOR, Constants.SP.DEFAULT.QUOTE_COLOR);
        boolean redRise = quoteColorType == 0;

        if (lgGreen == null) {
            lgGreen = new LinearGradient(0, 0, getWidth(), 0, new int[]{Color.TRANSPARENT, ContextCompat.getColor(getContext(), R.color.chart_green_h_border)}, null, Shader.TileMode.CLAMP);
        }
        if (lgRed == null) {
            lgRed = new LinearGradient(0, 0, getWidth(), 0, new int[]{Color.TRANSPARENT, ContextCompat.getColor(getContext(), R.color.chart_red_h_border)}, null, Shader.TileMode.CLAMP);
        }
        if (state < 0) {
            //down
            paint.setShader(redRise ? lgGreen : lgRed);
        } else if (state > 0) {
            //up
            paint.setShader(redRise ? lgRed : lgGreen);
        }
        if (valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        valueAnimator.setFloatValues(20f, 1f);
        valueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        if (clipPath.isEmpty()) {
            clipPath.moveTo(0, borderRadius);
            clipPath.quadTo(0, 0, borderRadius, 0);
            clipPath.lineTo(w - borderRadius, 0);
            clipPath.quadTo(w, 0, w, borderRadius);
            clipPath.lineTo(w, h - borderRadius);
            clipPath.quadTo(w, h, w - borderRadius, h);
            clipPath.lineTo(borderRadius, h);
            clipPath.quadTo(0, h, 0, h - borderRadius);
            clipPath.close();
        }
        canvas.drawColor(bg);
        canvas.clipPath(clipPath);
        if (state != 0) {
            rect.left = 0;
            rect.top = 0;
            rect.right = (int) (animationValue * w);
            rect.bottom = h;
            canvas.drawRect(rect, paint);
        }
    }
}

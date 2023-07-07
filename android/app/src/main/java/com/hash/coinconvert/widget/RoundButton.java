package com.hash.coinconvert.widget;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import com.hash.coinconvert.R;

public class RoundButton extends AppCompatTextView {

    private RoundButtonDrawable roundButtonDrawable;

    public RoundButton(Context context) {
        super(context);
        init(context, null, 0);
    }

    public RoundButton(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.RoundButtonStyle);
        init(context, attrs, R.attr.RoundButtonStyle);
    }

    public RoundButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        setClickable(true);
        setFocusable(true);
        roundButtonDrawable = RoundButtonDrawable.createFromAttr(context, attrs, defStyleAttr);
        setBackgroundKeepingPadding(this, roundButtonDrawable);

    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if(roundButtonDrawable != null) {
            setBackgroundKeepingPadding(this, roundButtonDrawable);
        }
    }

    /**
     * 设置背景颜色
     *
     * @param color
     */
    @Override
    public void setBackgroundColor(int color) {
        roundButtonDrawable.setColor(color);
    }

    /**
     * 设置四个角的半径
     *
     * @param radius
     */
    public void setRadius(int radius) {
        roundButtonDrawable.setCornerRadius(radius);
    }

    /**
     * 设置 每一个角的半径
     *
     * @param topLeftRadius     左上角半径
     * @param topRightRadius    右上角半径
     * @param bottomLeftRadius  右下角半径
     * @param bottomRightRadius 左下角半径
     */
    public void setEachCornerRadius(int topLeftRadius, int topRightRadius, int bottomLeftRadius, int bottomRightRadius) {
        float[] radius = new float[]{
                topLeftRadius, topLeftRadius,
                topRightRadius, topRightRadius,
                bottomRightRadius, bottomRightRadius,
                bottomLeftRadius, bottomLeftRadius
        };

        roundButtonDrawable.setCornerRadii(radius);
    }

    /**
     * 设置渐变
     *
     * @param gradientType 渐变类型
     * @param orientation  渐变方向
     * @param colors       渐变颜色
     */
    public void setGradient(int gradientType, GradientDrawable.Orientation orientation, int[] colors) {
        roundButtonDrawable.setGradientType(gradientType);
        roundButtonDrawable.setOrientation(orientation);
        roundButtonDrawable.setColors(colors);
    }

    public static void setBackgroundKeepingPadding(View view, RoundButtonDrawable drawable) {
        int[] padding = new int[]{view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom()};
        view.setBackground(drawable.getDrawable(view));
        view.setPadding(padding[0], padding[1], padding[2], padding[3]);
    }
}

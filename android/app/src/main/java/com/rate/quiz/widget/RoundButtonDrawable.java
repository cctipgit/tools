package com.rate.quiz.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.rate.quiz.R;

public class RoundButtonDrawable extends GradientDrawable {

    private boolean ripple;
    private GradientDrawable bgDrawable;

    public static RoundButtonDrawable createFromAttr(Context context, AttributeSet attr, int defStyleAttr) {
        TypedArray ta = context.obtainStyledAttributes(attr, R.styleable.RoundButton, defStyleAttr, R.style.RoundButton);
        int bgColor = ta.getColor(R.styleable.RoundButton_rb_background, Color.TRANSPARENT);
        ColorStateList bgColorStateList = ta.getColorStateList(R.styleable.RoundButton_rb_background);
        int radius = ta.getDimensionPixelSize(R.styleable.RoundButton_rb_radius, 0);
        boolean ripple = ta.getBoolean(R.styleable.RoundButton_rb_ripple, true);
        ta.recycle();

        RoundButtonDrawable drawable = new RoundButtonDrawable();
        drawable.ripple = ripple;
        if (bgColorStateList == null) {
            drawable.setColor(bgColor);
        } else {
            drawable.setColor(bgColorStateList);
        }
        drawable.initBackgroundDrawable(bgColorStateList == null ? bgColor : bgColorStateList.getDefaultColor(), radius);
        drawable.setCornerRadius(radius);
        return drawable;
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private ColorStateList getPressedColorSelector(int normalColor, int pressedColor) {
        return new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_pressed},
                        new int[]{android.R.attr.state_focused},
                        new int[]{android.R.attr.state_activated},
                        new int[]{}
                },
                new int[]{
                        pressedColor,
                        pressedColor,
                        pressedColor,
                        normalColor
                }
        );
    }

    private void initBackgroundDrawable(int color, int radius) {
        bgDrawable = new GradientDrawable();
        bgDrawable.setColor(color);
        bgDrawable.setCornerRadius(radius);
    }

    public Drawable getDrawable(View view) {
        if (ripple && view.isEnabled()) {
            return new RippleDrawable(getPressedColorSelector(getColor().getDefaultColor(), Integer.MAX_VALUE),
                    bgDrawable, null);
        } else {
            return this;
        }
    }
}

package com.rate.quiz.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.rate.quiz.R;
import com.rate.quiz.entity.TokenWrapper;

public class TokenView extends LinearLayout {

    private TokenWrapper token;

    public TokenView(Context context) {
        super(context);
    }

    public TokenView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TokenView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.item_home_currency_list, this);
    }
}

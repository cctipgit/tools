package com.rate.quiz.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.duxl.baselib.utils.DisplayUtil;
import com.rate.quiz.R;

public class TokenDetailStatItemView extends LinearLayout {

    private TextView titleView;
    private TextView valueView;

    public TokenDetailStatItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);

        int paddingV = DisplayUtil.dip2px(context, 8f);
        int paddingH = paddingV * 2;
        setPadding(paddingH, paddingV, paddingH, paddingV);
        setBackgroundResource(R.drawable.bg_token_detail_stat_item);
        addView(genTitleView(context));
        addView(genValueView(context));
    }

    private TextView genTitleView(Context context) {
        TextView textView = new TextView(context);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        textView.setTypeface(ResourcesCompat.getFont(context, R.font.roboto_medium));
        textView.setTextColor(ContextCompat.getColor(context, R.color.sub_title));
        if (isInEditMode()) {
            textView.setText("Title");
        }
        titleView = textView;
        return textView;
    }

    private TextView genValueView(Context context) {
        TextView textView = new TextView(context);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        textView.setTextColor(ContextCompat.getColor(context, R.color.theme_text_color));
        textView.setTypeface(ResourcesCompat.getFont(context, R.font.roboto_regular));
        textView.setText("---");
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = DisplayUtil.dip2px(context, 4f);
        textView.setLayoutParams(params);
        valueView = textView;
        return textView;
    }

    public void setTitle(CharSequence title) {
        titleView.setText(title);
    }

    public void setTitle(@StringRes int resId) {
        titleView.setText(resId);
    }

    public void setValue(CharSequence value) {
        valueView.setText(value);
    }
}

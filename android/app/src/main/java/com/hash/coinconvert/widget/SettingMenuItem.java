package com.hash.coinconvert.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.hash.coinconvert.R;

public class SettingMenuItem extends ConstraintLayout {

    private TextView tvTitle;
    private TextView tvValue;

    public SettingMenuItem(@NonNull Context context) {
        this(context, null);
    }

    public SettingMenuItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingMenuItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        try (TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SettingMenuItem)) {
            String title = ta.getString(R.styleable.SettingMenuItem_sm_title);
            if (TextUtils.isEmpty(title) && isInEditMode()) {
                title = "Title";
            }
            tvTitle.setText(title);
        }
    }

    private void init(Context context) {
        View.inflate(context, R.layout.view_setting_menu_item, this);
        tvTitle = findViewById(R.id.tv_title);
        tvValue = findViewById(R.id.tv_value);
    }

    public void setValue(CharSequence s) {
        tvValue.setText(s);
    }
}

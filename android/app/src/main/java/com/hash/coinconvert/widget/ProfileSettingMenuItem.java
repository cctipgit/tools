package com.hash.coinconvert.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.duxl.baselib.utils.DisplayUtil;
import com.hash.coinconvert.R;

public class ProfileSettingMenuItem extends ConstraintLayout {
    public ProfileSettingMenuItem(@NonNull Context context) {
        this(context, null);
    }

    public ProfileSettingMenuItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProfileSettingMenuItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ProfileSettingMenuItem);
        String title = ta.getString(R.styleable.ProfileSettingMenuItem_psm_Title);
        if (TextUtils.isEmpty(title) && isInEditMode()) {
            title = "Title";
        }
        ta.recycle();

        addView(genTitle(context, title));
        addView(genArrow(context));
//        setBackgroundResource(android.R.drawable.b);
    }

    private TextView genTitle(Context context, String title) {
        TextView textView = new TextView(context);
        textView.setTypeface(ResourcesCompat.getFont(context, R.font.roboto_medium));
        textView.setText(title);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftToLeft = LayoutParams.PARENT_ID;
        params.topToTop = LayoutParams.PARENT_ID;
        params.bottomToBottom = LayoutParams.PARENT_ID;
        textView.setLayoutParams(params);
        return textView;
    }

    private ImageView genArrow(Context context) {
        ImageView imageView = new ImageView(context);
        int size = DisplayUtil.dip2px(context, 24f);
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_arrow_right_menu_setting);
        drawable.setBounds(0, 0, size, size);
        imageView.setImageDrawable(drawable);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.rightToRight = LayoutParams.PARENT_ID;
        params.topToTop = LayoutParams.PARENT_ID;
        params.bottomToBottom = LayoutParams.PARENT_ID;
        imageView.setLayoutParams(params);

        return imageView;
    }
}

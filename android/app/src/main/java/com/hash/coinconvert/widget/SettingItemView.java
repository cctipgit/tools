package com.hash.coinconvert.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.duxl.baselib.utils.NullUtils;
import com.hash.coinconvert.R;
import com.hash.coinconvert.databinding.LayoutSettingItemViewBinding;

/**
 * 设置View
 */
public class SettingItemView extends FrameLayout {

    private LayoutSettingItemViewBinding mBinding;
    private String mLeftText;
    private String mRightText;
    private boolean mShowMore;
    private boolean mShowPress;
    private boolean mShowSwitch;

    public SettingItemView(@NonNull Context context) {
        this(context, null);
    }

    public SettingItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingItemView);
        if (typedArray != null) {
            mLeftText = typedArray.getString(R.styleable.SettingItemView_leftText);
            mRightText = typedArray.getString(R.styleable.SettingItemView_rightText);
            mShowMore = typedArray.getBoolean(R.styleable.SettingItemView_showMore, true);
            mShowPress = typedArray.getBoolean(R.styleable.SettingItemView_showPress, true);
            mShowSwitch = typedArray.getBoolean(R.styleable.SettingItemView_showSwitch, false);
            typedArray.recycle();
        }

        View view = LayoutInflater.from(context).inflate(R.layout.layout_setting_item_view, this, false);
        mBinding = LayoutSettingItemViewBinding.bind(view);
        addView(view);

        mBinding.tvLeft.setText(NullUtils.format(mLeftText));
        mBinding.tvRight.setText(NullUtils.format(mRightText));
        mBinding.ivMore.setVisibility(mShowMore ? View.VISIBLE : View.GONE);
        mBinding.vSwitch.setVisibility(mShowSwitch ? View.VISIBLE : View.GONE);
        if (mShowPress) {
            mBinding.getRoot().getDelegate().setBackgroundPressColor(getResources().getColor(R.color.setting_item_press));
        }
    }

    public TextView getLeftText() {
        return mBinding.tvLeft;
    }

    public TextView getRightText() {
        return mBinding.tvRight;
    }

    public Switch getSwitchView() {
        return mBinding.vSwitch;
    }

    public ImageView getMoreView() {
        return mBinding.ivMore;
    }
}

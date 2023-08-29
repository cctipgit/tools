package com.rate.quiz.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.duxl.baselib.utils.SPUtils;
import com.flyco.roundview.RoundFrameLayout;
import com.flyco.roundview.RoundTextView;
import com.rate.quiz.Constants;
import com.rate.quiz.R;
import com.rate.quiz.databinding.LayoutDateSwitchViewBinding;
import com.rate.quiz.utils.FontUtil;

/**
 * 汇率详情时间选择控件(1Day,1Week,1Month,1YEAR, ALL)
 */
public class DateSwitchView extends RoundFrameLayout {

    public LayoutDateSwitchViewBinding mBinding;
    public int mSelectPosition = 0;

    public DateSwitchView(Context context) {
        this(context, null);
    }

    private final int normalTextColor;
    private final int normalTextViewBg;
    private final int selectedTextColor;
    private final int selectedTextViewBg;


    public DateSwitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_date_switch_view, this, false);
        mBinding = LayoutDateSwitchViewBinding.bind(view);
        addView(view);

        mBinding.tvDay.setOnClickListener(v -> switchChange(0));
        mBinding.tvWeek.setOnClickListener(v -> switchChange(1));
        mBinding.tvMonth.setOnClickListener(v -> switchChange(2));
        mBinding.tvYear.setOnClickListener(v -> switchChange(3));
        mBinding.tvAll.setOnClickListener(v -> switchChange(4));

        FontUtil.setType(mBinding.tvDay, FontUtil.FOUNT_TYPE.POPPINS_MEDIUM);
        FontUtil.setType(mBinding.tvWeek, FontUtil.FOUNT_TYPE.POPPINS_MEDIUM);
        FontUtil.setType(mBinding.tvMonth, FontUtil.FOUNT_TYPE.POPPINS_MEDIUM);
        FontUtil.setType(mBinding.tvYear, FontUtil.FOUNT_TYPE.POPPINS_MEDIUM);
        FontUtil.setType(mBinding.tvAll, FontUtil.FOUNT_TYPE.POPPINS_MEDIUM);

        normalTextColor = ContextCompat.getColor(context, R.color.theme_text_color);
        normalTextViewBg = Color.TRANSPARENT;

        selectedTextColor = ContextCompat.getColor(context, R.color.rate_detail_tab_selected_text);
        selectedTextViewBg = ContextCompat.getColor(context, R.color.rate_detail_tab_selected_bg);
    }

    public int getSelectPosition() {
        return mSelectPosition;
    }

    public void switchChange(int position) {
        if (position == mSelectPosition) return;
        SPUtils.getInstance().put(Constants.SP.KEY.SWIFTH_STATUS, position);
        updateItemState((RoundTextView) mBinding.dateSwitchViewWrapper.getChildAt(mSelectPosition), false);
        updateItemState((RoundTextView) mBinding.dateSwitchViewWrapper.getChildAt(position), true);
        mSelectPosition = position;
        if (mOnSwitchChangedListener != null) {
            mOnSwitchChangedListener.onSwitchChanged(mSelectPosition);
        }
    }

    private void updateItemState(RoundTextView view, boolean selected) {
        view.getDelegate().setBackgroundColor(selected ? selectedTextViewBg : normalTextViewBg);
        view.setTextColor(selected ? selectedTextColor : normalTextColor);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mBinding = null;
    }

    public interface OnSwitchChangedListener {
        void onSwitchChanged(int position);
    }

    public OnSwitchChangedListener mOnSwitchChangedListener;

    public void setOnSwitchChangedListener(OnSwitchChangedListener listener) {
        this.mOnSwitchChangedListener = listener;
    }
}

package com.hash.coinconvert.ui.activity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;

import com.duxl.baselib.utils.SPUtils;
import com.gw.swipeback.SwipeBackLayout;
import com.hash.coinconvert.Constants;
import com.hash.coinconvert.R;
import com.hash.coinconvert.databinding.ActivitySettingQuoteColorBinding;
import com.hash.coinconvert.utils.Dispatch;

/**
 * 行情涨跌颜色设置
 */
public class SettingQuoteColorActivity extends BaseActivity {

    private ActivitySettingQuoteColorBinding mBinding;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_setting_quote_color;
    }

    @Override
    protected void initView(View v) {
        super.initView(v);
        getActionBarView().setBackImage(R.drawable.ic_actionbar_arrow_left);
        getActionBarView().getIvBack().setImageTintList(ColorStateList.valueOf(Color.WHITE));
        setTitle(R.string.quote_color);
        setDialogStyle();
        setSlideEnable(SwipeBackLayout.FROM_TOP);
        setObserveOtherSlideFinish();
        mBinding = ActivitySettingQuoteColorBinding.bind(v);
        int checkValue = SPUtils.getInstance().getInt(Constants.SP.KEY.QUOTE_COLOR, Constants.SP.DEFAULT.QUOTE_COLOR);
        updateRadioButtons(checkValue);
        mBinding.rbRedUp.setOnClickListener(view -> {
            SPUtils.getInstance().put(Constants.SP.KEY.QUOTE_COLOR, 0);
            updateRadioButtons(0);
            delayFinish();
        });
        mBinding.rbGreenUp.setOnClickListener(view -> {
            SPUtils.getInstance().put(Constants.SP.KEY.QUOTE_COLOR, 1);
            updateRadioButtons(1);
            delayFinish();
        });
    }

    private void delayFinish() {
        Dispatch.I.postUIDelayed(() -> {
            setResult2(RESULT_OK);
            finish();
        }, 100L);
    }

    private void updateRadioButtons(int quoteColorValue) {
        if (quoteColorValue == 0) {
            mBinding.rbGreenUp.setChecked(false);
            mBinding.rbRedUp.setChecked(true);
        } else {
            mBinding.rbGreenUp.setChecked(true);
            mBinding.rbRedUp.setChecked(false);
        }
    }

    @Override
    protected void onClickActionBack(View v) {
        super.onClickActionBack(v);
        slideOutRight();
    }
}

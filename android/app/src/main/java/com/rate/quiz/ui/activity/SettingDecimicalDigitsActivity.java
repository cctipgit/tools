package com.rate.quiz.ui.activity;

import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResult;

import com.duxl.baselib.utils.SPUtils;
import com.gw.swipeback.SwipeBackLayout;
import com.rate.quiz.Constants;
import com.rate.quiz.R;
import com.rate.quiz.databinding.ActivitySettingDecimicalDigitsBinding;

/**
 * 法定货币和加密货币小数位设置
 */
public class SettingDecimicalDigitsActivity extends BaseActivity {

    private ActivitySettingDecimicalDigitsBinding mBinding;
    private final int mRequestCodeForLegal = 10;
    private final int mRequestCodeForCrypto = 11;

    @Override
    protected void initView(View v) {
        super.initView(v);
        setDialogStyle();
        setObserveOtherSlideFinish();
        setSlideEnable(SwipeBackLayout.FROM_TOP);
        setTitle(R.string.set_decimical_digits);
        mBinding = ActivitySettingDecimicalDigitsBinding.bind(v);
        // 法定货币
        mBinding.vLegalTender.setOnClickListener(view->{
            Bundle args = new Bundle();
            args.putString("type", Constants.SP.KEY.DECIMICAL_LEGAL_TENDER);
            goActivity(SettingDecimicalDigitsSwitchActivity.class, args, mRequestCodeForLegal);
            slideInRight();
        });
        // 加密货币
        mBinding.vCryptocurrency.setOnClickListener(view->{
            Bundle args = new Bundle();
            args.putString("type", Constants.SP.KEY.DECIMICAL_CRYPTOCURRENCY);
            goActivity(SettingDecimicalDigitsSwitchActivity.class, args, mRequestCodeForCrypto);
            slideInRight();
        });
        showInfo();
    }

    @Override
    protected void onClickActionBack(View v) {
        super.onClickActionBack(v);
        slideOutRight();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_setting_decimical_digits;
    }

    @Override
    protected void onActivityResult(int requestCode, ActivityResult result) {
        super.onActivityResult(requestCode, result);
        showInfo();
    }

    protected void showInfo() {
        int value1 = SPUtils.getInstance().getInt(Constants.SP.KEY.DECIMICAL_LEGAL_TENDER, Constants.SP.DEFAULT.DECIMICAL_LEGAL_TENDER);
        mBinding.vLegalTender.getRightText().setText(String.valueOf(value1));
        int value2 = SPUtils.getInstance().getInt(Constants.SP.KEY.DECIMICAL_CRYPTOCURRENCY, Constants.SP.DEFAULT.DECIMICAL_CRYPTOCURRENCY);
        mBinding.vCryptocurrency.getRightText().setText(String.valueOf(value2));
    }
}

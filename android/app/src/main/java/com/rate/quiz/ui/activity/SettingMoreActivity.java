package com.rate.quiz.ui.activity;

import android.view.View;

import androidx.activity.result.ActivityResult;

import com.duxl.baselib.utils.SPUtils;
import com.gw.swipeback.SwipeBackLayout;
import com.rate.quiz.Constants;
import com.rate.quiz.R;
import com.rate.quiz.databinding.ActivitySettingMoreBinding;

/**
 * 更多设置
 */
public class SettingMoreActivity extends BaseActivity {

    private ActivitySettingMoreBinding mBinding;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_setting_more;
    }

    @Override
    protected void initView(View v) {
        super.initView(v);
        setTitle(R.string.more_settings);
        setDialogStyle();
        setSlideEnable(SwipeBackLayout.FROM_TOP);
        setObserveOtherSlideFinish();
        mBinding = ActivitySettingMoreBinding.bind(v);
        // 行情颜色
        showQuoteColor();
        mBinding.vQuoteColor.setOnClickListener(view -> {
            goActivity(SettingQuoteColorActivity.class, 0);
            slideInRight();
        });

        // 键盘声音
        mBinding.vKeyboardSound.getSwitchView().setChecked(SPUtils.getInstance().getBoolean(Constants.SP.KEY.KEYBOARD_SOUND, Constants.SP.DEFAULT.KEYBOARD_SOUND));
        mBinding.vKeyboardSound.getSwitchView().setOnCheckedChangeListener((buttonView, isChecked) -> {
            SPUtils.getInstance().put(Constants.SP.KEY.KEYBOARD_SOUND, isChecked);
        });
    }

    @Override
    protected void onClickActionBack(View v) {
        super.onClickActionBack(v);
        slideOutRight();
    }

    @Override
    protected void onActivityResult(int requestCode, ActivityResult result) {
        super.onActivityResult(requestCode, result);
        showQuoteColor();
    }

    protected void showQuoteColor() {
        int quoteColorType = SPUtils.getInstance().getInt(Constants.SP.KEY.QUOTE_COLOR, Constants.SP.DEFAULT.QUOTE_COLOR);
        mBinding.vQuoteColor.getRightText().setText(quoteColorType == 0 ? R.string.red_rise_green_fall : R.string.green_rise_red_fall);
    }
}

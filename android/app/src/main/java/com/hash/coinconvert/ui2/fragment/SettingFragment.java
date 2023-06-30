package com.hash.coinconvert.ui2.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.duxl.baselib.utils.SPUtils;
import com.hash.coinconvert.Constants;
import com.hash.coinconvert.R;
import com.hash.coinconvert.base.BaseFragment;
import com.hash.coinconvert.databinding.FragmentSettingBinding;
import com.hash.coinconvert.ui.activity.SettingCurrencyValueActivity;
import com.hash.coinconvert.ui.activity.SettingDecimicalDigitsActivity;
import com.hash.coinconvert.ui.activity.SettingQuoteColorActivity;

public class SettingFragment extends BaseFragment {

    private static final int CODE_DEF_CURRENCY_VALUE = 1628;
    private static final int CODE_QUOTE_COLOR = 1629;
    private static final int CODE_DECIMALS = 1630;

    private FragmentSettingBinding binding;

    public SettingFragment() {
        super(R.layout.fragment_setting);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentSettingBinding.bind(view);

        updateView();

        binding.btnReset.setOnClickListener(v -> {
            SPUtils.getInstance().put(Constants.SP.KEY.DEFAULT_CURRENCY_VALUE, Constants.SP.DEFAULT.DEFAULT_CURRENCY_VALUE);
            SPUtils.getInstance().put(Constants.SP.KEY.QUOTE_COLOR, Constants.SP.DEFAULT.QUOTE_COLOR);
        });

        binding.menuDefValue.setOnClickListener(v -> {
//            getNavController().navigate(SettingFragmentDirections.actionFragmentSettingToActivityDefCurrencyValue());
            goActivity(SettingCurrencyValueActivity.class, null, CODE_DEF_CURRENCY_VALUE);
        });

        binding.menuPriceColor.setOnClickListener(v -> {
//            getNavController().navigate(SettingFragmentDirections.actionFragmentSettingToActivityQuoteColor());
            goActivity(SettingQuoteColorActivity.class, null, CODE_QUOTE_COLOR);
        });

        binding.menuPoint.setOnClickListener(v -> {
            goActivity(SettingDecimicalDigitsActivity.class, null, CODE_DECIMALS);
        });
    }

    @Override
    protected void onCommonActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onCommonActivityResult", "" + requestCode);
        if (resultCode == Activity.RESULT_OK) {
            updateView();
        }
    }

    private void updateView() {
        binding.menuDefValue.setValue(SPUtils.getInstance().getInt(Constants.SP.KEY.DEFAULT_CURRENCY_VALUE, Constants.SP.DEFAULT.DEFAULT_CURRENCY_VALUE) + "");
        int quoteColorType = SPUtils.getInstance().getInt(Constants.SP.KEY.QUOTE_COLOR, Constants.SP.DEFAULT.QUOTE_COLOR);
        binding.menuPriceColor.setValue(getString(quoteColorType == 0 ? R.string.red_rise_green_fall : R.string.green_rise_red_fall));
    }
}

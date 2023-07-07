package com.hash.coinconvert.ui2.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.duxl.baselib.utils.DisplayUtil;
import com.duxl.baselib.utils.SPUtils;
import com.hash.coinconvert.Constants;
import com.hash.coinconvert.R;
import com.hash.coinconvert.base.BaseMVVMFragment;
import com.hash.coinconvert.database.entity.Token;
import com.hash.coinconvert.databinding.FragmentTokenDetailsBinding;
import com.hash.coinconvert.entity.ChartItem;
import com.hash.coinconvert.ui.activity.QuoteCurrencyActivity;
import com.hash.coinconvert.utils.FlagLoader;
import com.hash.coinconvert.vm.TokenDetailsViewModel;
import com.hash.coinconvert.widget.KLineView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

public class TokenDetailsFragment extends BaseMVVMFragment<TokenDetailsViewModel, FragmentTokenDetailsBinding>
        implements KLineView.OnStatListener {
    public static final String TAG = "TokenDetailsFragment";

    private int interval = TimeType.DAY.value;
    private static final int CODE_SWITCH_CURRENCY = 1537;

    private String baseToken;
    private String quoteToken;
    private String currencyUnit;

    public TokenDetailsFragment() {
        super(R.layout.fragment_token_details);
    }

    @NonNull
    @Override
    protected FragmentTokenDetailsBinding bindView(View view) {
        return FragmentTokenDetailsBinding.bind(view);
    }

    @Override
    protected void initView() {
        baseToken = TokenDetailsFragmentArgs.fromBundle(getArguments()).getSymbol();
        initDefaultCurrency();
        initTimeType();
        Drawable drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_down);
        int size = DisplayUtil.dip2px(requireContext(), 16f);
        drawable.setBounds(0, 0, size, size);
        binding.tvQuote.setCompoundDrawables(null, null, drawable, null);
        binding.statHigh.setTitle(R.string.token_details_stat_high);
        binding.statLow.setTitle(R.string.token_details_stat_low);
        binding.statAvg.setTitle(R.string.token_details_stat_avg);
        binding.statChange.setTitle(R.string.token_details_stat_change);
        binding.kLineView.setOnStatListener(this);
        binding.tvQuote.setOnClickListener(v -> {
            goActivity(QuoteCurrencyActivity.class, null, CODE_SWITCH_CURRENCY);
        });
    }

    @Override
    protected void onCommonActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onCommonActivityResult", requestCode + "," + resultCode + "," + data);
        if (requestCode == CODE_SWITCH_CURRENCY && resultCode == Activity.RESULT_OK) {
            quoteToken = data.getStringExtra("data");
            currencyUnit = data.getStringExtra("unit");
            saveDefaultCurrency();
            loadData();
        }
    }

    @Override
    protected void onFirstResume() {
        super.onFirstResume();
        loadData();
    }

    @Override
    public Class<? extends TokenDetailsViewModel> getVMClass() {
        return TokenDetailsViewModel.class;
    }

    private void initTimeType() {
        TimeType[] timeTypes = TimeType.values();
        int rbCount = binding.flex.getChildCount();
        assert rbCount == timeTypes.length;
        for (int i = 0; i < rbCount; i++) {
            RadioButton rb = (RadioButton) binding.flex.getChildAt(i);
            rb.setText(timeTypes[i].text);
            rb.setTag(timeTypes[i].value);
            rb.setOnClickListener(onRadioButtonClickListener);
        }
        RadioButton rb = (RadioButton) binding.flex.getChildAt(0);
        rb.setChecked(true);
    }

    private final View.OnClickListener onRadioButtonClickListener = v -> {
        RadioButton button = ((RadioButton) v);
        int interval = (int) button.getTag();
        if (interval == TokenDetailsFragment.this.interval) return;
        for (int i = 0; i < binding.flex.getChildCount(); i++) {
            RadioButton b = (RadioButton) binding.flex.getChildAt(i);
            if (b.isChecked() && b != button) {
                b.setChecked(false);
                TokenDetailsFragment.this.interval = interval;
                loadData();
                break;
            }
        }
    };

    @Override
    protected void observer() {
        super.observer();
        observer(viewModel.getUIData(), uiData -> {
            if (uiData.data != null) {
                binding.kLineView.setData(uiData.data);
            }
            Optional.ofNullable(uiData.base).ifPresent(t -> {
                FlagLoader.load(binding.imgBase, 0, t.icon);
                binding.tvBase.setText(t.token);
            });
            Optional.ofNullable(uiData.quote).ifPresent(t -> {
                FlagLoader.load(binding.imgQuote, 0, t.icon);
                binding.tvQuote.setText(t.token);
                currencyUnit = t.unitName;
                Log.d(TAG, "quote:" + t.token + "," + t.unitName);
            });
        });
    }

    private void saveDefaultCurrency() {
        SPUtils.getInstance().put(Constants.SP.KEY.DEFAULT_CURRENCY_SYMBOL, quoteToken);
    }

    private void initDefaultCurrency() {
        String symbol = SPUtils.getInstance().getString(Constants.SP.KEY.DEFAULT_CURRENCY_SYMBOL);
        if (!TextUtils.isEmpty(symbol)) {
            this.quoteToken = symbol;
        } else {
            this.quoteToken = "USD";
        }
    }

    @Override
    protected void onLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onStat(float high, float low, float avg, float change) {
        binding.statHigh.setValue(formatNumber(high));
        binding.statLow.setValue(formatNumber(low));
        binding.statAvg.setValue(formatNumber(avg));
        binding.statChange.setValue(formatNumber(change));
    }

    private String formatNumber(float f) {
        return currencyUnit + " " + BigDecimal.valueOf(f).setScale(6, RoundingMode.DOWN).toString();
    }

    private void loadData() {
        viewModel.fetchData(baseToken, quoteToken, this.interval);
    }

    private enum TimeType {
        DAY(1, "1D"), WEEK(2, "1W"), MONTH(3, "1M"), YEAR(4, "1Y");
        /**
         * for server api
         */
        final int value;
        /**
         * for page content
         */
        final String text;

        TimeType(int value, String text) {
            this.value = value;
            this.text = text;
        }
    }

    public static class UIData {
        public Token base;
        public Token quote;
        public List<ChartItem> data;
    }
}

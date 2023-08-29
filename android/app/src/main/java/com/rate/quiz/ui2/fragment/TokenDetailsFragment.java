package com.rate.quiz.ui2.fragment;

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
import com.rate.quiz.Constants;
import com.rate.quiz.R;
import com.rate.quiz.base.BaseMVVMFragment;
import com.rate.quiz.database.entity.Token;
import com.rate.quiz.databinding.FragmentTokenDetailsBinding;
import com.rate.quiz.entity.ChartItem;
import com.rate.quiz.ui.activity.QuoteCurrencyActivity;
import com.rate.quiz.utils.FlagLoader;
import com.rate.quiz.utils.task.TaskHelper;
import com.rate.quiz.vm.TokenDetailsViewModel;
import com.rate.quiz.widget.KLineView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
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
    protected void showLoading() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void hideLoading() {
        binding.progressBar.setVisibility(View.GONE);
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
        TaskHelper.viewTokenDetail();
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
            rb.setText(timeTypes[i].text.toUpperCase(Locale.ROOT));
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
        if (binding.progressBar.getVisibility() == View.VISIBLE) {
            button.setChecked(false);
            return;
        }
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
        viewModel.fetchData(baseToken, quoteToken, TimeType.valueOf(interval).text);
    }

    private enum TimeType {
        DAY(1, "1d"), WEEK(2, "7d"), MONTH(3, "1m"), YEAR(4, "1y");
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

        static TimeType valueOf(int interval) {
            for (TimeType t : values()) {
                if (t.value == interval) return t;
            }
            return DAY;
        }
    }

    public static class UIData {
        public Token base;
        public Token quote;
        public List<ChartItem> data;
    }
}

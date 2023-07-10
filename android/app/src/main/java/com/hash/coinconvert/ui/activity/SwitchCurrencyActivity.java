package com.hash.coinconvert.ui.activity;

import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.duxl.baselib.utils.EmptyUtils;
import com.duxl.baselib.utils.SPUtils;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.hash.coinconvert.Constants;
import com.hash.coinconvert.R;
import com.hash.coinconvert.database.repository.TokenRepository;
import com.hash.coinconvert.databinding.ActivitySwitchCurrencyBinding;
import com.hash.coinconvert.ui.adapter.SwitchCurrencyAdapter;
import com.hash.coinconvert.utils.Dispatch;
import com.hash.coinconvert.widget.SearchView;
import com.hash.coinconvert.widget.SideBar;

import java.util.List;

/**
 * 切换货币
 */
public class SwitchCurrencyActivity extends SearchCurrencyActivity {

    private final String TAG = "SwitchCurrencyActivity";

    private ActivitySwitchCurrencyBinding mBinding;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_switch_currency;
    }

    @Override
    protected void initView(View v) {
        mBinding = ActivitySwitchCurrencyBinding.bind(v);
        super.initView(v);
        hideActionBar();
    }

    @Override
    protected SearchView getSearchView() {
        return mBinding.searchView;
    }

    @Override
    protected SideBar getSideBar() {
        return mBinding.sideBar;
    }

    @Override
    protected List<String> getCheckedTokens() {
        String tokensJson = SPUtils.getInstance().getString(Constants.SP.KEY.HOME_TOKEN_LIST);
        if (EmptyUtils.isNotEmpty(tokensJson)) {
            List<String> tokens = new Gson().fromJson(tokensJson, new TypeToken<List<String>>() {
            }.getType());
            return tokens;
        }
        return null;
    }

    @Override
    protected BaseQuickAdapter getAdapter() {
        SwitchCurrencyAdapter adapter = (SwitchCurrencyAdapter) super.getAdapter();
        adapter.setOnItemClickListener((adp, view, position) -> {
            SwitchCurrencyAdapter.ItemEntity item = adapter.getItemOrNull(position);
            if (item != null && item.token != null) {
                item.token.revertFavorite();
                item.isChecked = !item.isChecked;
                Dispatch.I.submit(() -> TokenRepository.updateFavorite(item.token.token, item.token.isFavorite()));
                adapter.notifyItemChanged(adapter.getItemPosition(item), SwitchCurrencyAdapter.PAYLOAD_UPDATE_CHECK);
            }
        });
        return adapter;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        slideOutLeft();
    }

    @Override
    public void finish() {
        super.finish();
        slideOutLeft();
    }
}

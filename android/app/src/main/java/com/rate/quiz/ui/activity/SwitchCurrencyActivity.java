package com.rate.quiz.ui.activity;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.duxl.baselib.utils.DisplayUtil;
import com.duxl.baselib.utils.EmptyUtils;
import com.duxl.baselib.utils.SPUtils;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.rate.quiz.Constants;
import com.rate.quiz.R;
import com.rate.quiz.database.repository.TokenRepository;
import com.rate.quiz.databinding.ActivitySwitchCurrencyBinding;
import com.rate.quiz.ui.adapter.SwitchCurrencyAdapter;
import com.rate.quiz.ui2.adapter.SpaceDecoration;
import com.rate.quiz.utils.Dispatch;
import com.rate.quiz.utils.task.TaskHelper;
import com.rate.quiz.widget.SearchView;
import com.rate.quiz.widget.SideBar;

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
        mSmartRecyclerView.getContentView().addItemDecoration(new SpaceDecoration(0, 0, 0, DisplayUtil.dip2px(this, 8)));
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
                if (item.isChecked) {
                    TaskHelper.addToken();
                }
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

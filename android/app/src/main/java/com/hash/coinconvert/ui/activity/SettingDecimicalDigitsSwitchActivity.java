package com.hash.coinconvert.ui.activity;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.duxl.baselib.utils.SPUtils;
import com.gw.swipeback.SwipeBackLayout;
import com.hash.coinconvert.Constants;
import com.hash.coinconvert.R;
import com.hash.coinconvert.ui.adapter.SettingListAdapter;
import com.hash.coinconvert.utils.AmountUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 小数位选中页面
 */
public class SettingDecimicalDigitsSwitchActivity extends BaseRecyclerViewActivity implements OnItemClickListener {

    private SettingListAdapter mAdapter;
    private String mType = Constants.SP.KEY.DECIMICAL_LEGAL_TENDER; // 法定货币、数字货币

    @Override
    protected void initParams(Intent args) {
        super.initParams(args);
        if (args != null) {
            mType = args.getStringExtra("type");
        }
    }

    @Override
    protected void initView(View v) {
        super.initView(v);
        setDialogStyle();
        setSlideEnable(SwipeBackLayout.FROM_TOP);
        setObserveOtherSlideFinish();
        if (Constants.SP.KEY.DECIMICAL_LEGAL_TENDER.equals(mType)) {
            setTitle(R.string.legal_tender);
        } else {
            setTitle(R.string.cryptocurrency);
        }
    }

    @Override
    protected void onClickActionBack(View v) {
        super.onClickActionBack(v);
        slideOutRight();
    }

    @Override
    protected BaseQuickAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new SettingListAdapter();

            int max = 0; // 最大小数位数
            int checkedDecimal = 0; // 已选择的小数位位数
            if (Constants.SP.KEY.DECIMICAL_LEGAL_TENDER.equals(mType)) {
                max = 8;
                checkedDecimal = SPUtils.getInstance().getInt(Constants.SP.KEY.DECIMICAL_LEGAL_TENDER, Constants.SP.DEFAULT.DECIMICAL_LEGAL_TENDER);
            } else {
                max = 12;
                checkedDecimal = SPUtils.getInstance().getInt(Constants.SP.KEY.DECIMICAL_CRYPTOCURRENCY, Constants.SP.DEFAULT.DECIMICAL_CRYPTOCURRENCY);
            }

            List<SettingListAdapter.ListItem> list = new ArrayList();
            for (int i = 0; i <= max; i += 2) {
                list.add(newItem(i, i == checkedDecimal));
            }
            mAdapter.setNewInstance(list);
            mAdapter.setOnItemClickListener(this);
        }
        return mAdapter;
    }

    /**
     * @param decimal 小数位
     * @return
     */
    private SettingListAdapter.ListItem newItem(int decimal, boolean checked) {
        String example = AmountUtils.formatAmount(BigDecimal.valueOf(1234), decimal);
        String decimalStr = decimal == 0 ? "None" : String.valueOf(decimal);
        return new SettingListAdapter.ListItem(decimalStr, example, checked).setTag(decimal);
    }

    @Override
    protected void loadData(int pageNum) {

    }

    @Override
    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
        Integer tag = (Integer) mAdapter.getItem(position).tag;
        if (Constants.SP.KEY.DECIMICAL_LEGAL_TENDER.equals(mType)) {
            SPUtils.getInstance().put(Constants.SP.KEY.DECIMICAL_LEGAL_TENDER, tag);
        } else {
            SPUtils.getInstance().put(Constants.SP.KEY.DECIMICAL_CRYPTOCURRENCY, tag);
        }
        finish();
    }
}

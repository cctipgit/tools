package com.hash.coinconvert.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;

import com.duxl.baselib.utils.DisplayUtil;
import com.duxl.baselib.utils.SPUtils;
import com.duxl.baselib.utils.ToastUtils;
import com.duxl.baselib.utils.Utils;
import com.gw.swipeback.SwipeBackLayout;
import com.hash.coinconvert.BuildConfig;
import com.hash.coinconvert.Constants;
import com.hash.coinconvert.R;
import com.hash.coinconvert.databinding.ActivitySettingBinding;
import com.hash.coinconvert.http.SocketSender;
import com.hash.coinconvert.ui.fragment.dialog.AlertDialog;
import com.hash.coinconvert.utils.Dispatch;
import com.hash.coinconvert.utils.FontUtil;

public class SettingActivity extends BaseActivity {

    private final String TAG = "SettingActivity";

    private ActivitySettingBinding mBinding;
    private final int mRequestCodeForCurrencyValue = 10;
    private boolean mCurrentLocalCurrency;

    @SuppressLint("NewApi")
    public ActivityResultLauncher<String[]> mPermissionMutLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), map -> {
        Boolean accessCoarseLocation = map.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
        Boolean accessFineLocation = map.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
        Log.i(TAG, "位置权限: accessCoarseLocation=" + accessCoarseLocation + ", accessFineLocation=" + accessFineLocation);
        if (!accessCoarseLocation || !accessFineLocation) {
            mBinding.vLocalCurrency.getSwitchView().setChecked(false);
        }
    });

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView(View v) {
        super.initView(v);
        setDialogStyle();
        setObserveOtherSlideFinish();
        setSlideEnable(SwipeBackLayout.FROM_TOP);
        setTitle(R.string.converter_setting);
        mBinding = ActivitySettingBinding.bind(v);

        // 是否显示当地货币（定位图标）
        mCurrentLocalCurrency = SPUtils.getInstance().getBoolean(Constants.SP.KEY.DEFAULT_LOCAL_CURRENCY, Constants.SP.DEFAULT.DEFAULT_LOCAL_CURRENCY);
        mBinding.vLocalCurrency.getSwitchView().setChecked(mCurrentLocalCurrency);
        mBinding.vLocalCurrency.getSwitchView().setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION) && hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    SPUtils.getInstance().put(Constants.SP.KEY.DEFAULT_LOCAL_CURRENCY, true);
                } else {
                    Dispatch.I.postUIDelayed(() -> mBinding.vLocalCurrency.getSwitchView().setChecked(false), 500);
                    boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);
                    if (showRationale) {
                        mPermissionMutLauncher.launch(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION});
                    } else {
                        AlertDialog
                                .newInstance()
                                .setTitle(R.string.location_services)
                                .setMessage(Utils.getString(R.string.location_tips, Utils.getString(R.string.app_name)))
                                .setRightButton(R.string.ok, null)
                                .showDialog(getSupportFragmentManager());
                    }
                }
            } else {
                SPUtils.getInstance().put(Constants.SP.KEY.DEFAULT_LOCAL_CURRENCY, false);
            }

        });

        // 是否显示货币符号
        mBinding.vCurrencySymbol.getSwitchView().setChecked(SPUtils.getInstance().getBoolean(Constants.SP.KEY.DEFAULT_CURRENCY_SYMBOL, Constants.SP.DEFAULT.DEFAULT_CURRENCY_SYMBOL));
        mBinding.vCurrencySymbol.getSwitchView().setOnCheckedChangeListener((buttonView, isChecked) -> {
            SPUtils.getInstance().put(Constants.SP.KEY.DEFAULT_CURRENCY_SYMBOL, isChecked);
        });

        // 默认货币数量
        showDefaultCurrencyValue();
        mBinding.vDefaultCurrencyValue.setOnClickListener(view -> {
            goActivity(SettingCurrencyValueActivity.class, mRequestCodeForCurrencyValue);
            slideInRight();
        });
        // 小数位设置
        mBinding.vSetDecimicalDigits.setOnClickListener(view -> {
            goActivity(SettingDecimicalDigitsActivity.class);
            slideInRight();
        });
        // 更多设置
        mBinding.vMoreSettings.setOnClickListener(view -> {
            goActivity(SettingMoreActivity.class);
            slideInRight();
        });
        mBinding.vMoreSettings.setOnLongClickListener(view -> {
            if(BuildConfig.DEBUG) {
                goActivity(DebugActivity.class);
            }
            return true;
        });
        // 重置货币列表
        FontUtil.setType(mBinding.tvRestoreDefaultCurrencyList, FontUtil.FOUNT_TYPE.POPPINS_MEDIUM);
        mBinding.tvRestoreDefaultCurrencyList.setOnClickListener(view -> {
            AlertDialog
                    .newInstance()
                    .setMessage(R.string.restore_curreny_list_msg)
                    .setLeftButton(R.string.cancel, null)
                    .setRightButton(R.string.recovery, v1 -> {
                        // 获取首页token
                        String location = null;
                        if (SPUtils.getInstance().getBoolean(Constants.SP.KEY.DEFAULT_LOCAL_CURRENCY, Constants.SP.DEFAULT.DEFAULT_LOCAL_CURRENCY)) {
                            location = SPUtils.getInstance().getString(Constants.SP.KEY.LOCATION_COUNTRY_CODE);
                        }
                        int rateItemCount = SPUtils.getInstance().getInt(Constants.SP.KEY.RATE_ITEM_COUNT, Constants.SP.DEFAULT.RATE_ITEM_COUNT);
                        SocketSender.sendCurrentDefaultCurrency(rateItemCount, location);
                        SocketSender.sendCurrencyTokensList();
                    })
                    .showDialog(getSupportFragmentManager());
        });

    }

    @Override
    protected void onClickActionBack(View v) {
        super.onClickActionBack(v);
        slideOutBottom();
    }

    protected void showDefaultCurrencyValue() {
        int value = SPUtils.getInstance().getInt(Constants.SP.KEY.DEFAULT_CURRENCY_VALUE, Constants.SP.DEFAULT.DEFAULT_CURRENCY_VALUE);
        mBinding.vDefaultCurrencyValue.getRightText().setText(String.valueOf(value));
    }

    @Override
    protected void onActivityResult(int requestCode, ActivityResult result) {
        super.onActivityResult(requestCode, result);
        if (requestCode == mRequestCodeForCurrencyValue) {
            if (result.getResultCode() == RESULT_OK) {
                showDefaultCurrencyValue();
            }
        }
    }

    @Override
    public void finish() {
        boolean currentLocalCurrency = SPUtils.getInstance().getBoolean(Constants.SP.KEY.DEFAULT_LOCAL_CURRENCY, Constants.SP.DEFAULT.DEFAULT_LOCAL_CURRENCY);
        if (currentLocalCurrency && !mCurrentLocalCurrency) {
            // 进入设置页面当前定位是关闭的，退出设置页面当前定位是开启
            Intent data = new Intent();
            data.putExtra("local_currency", true);
            setResult2(RESULT_OK, data);
        }
        super.finish();
    }
}

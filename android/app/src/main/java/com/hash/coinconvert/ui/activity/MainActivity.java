package com.hash.coinconvert.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.location.Address;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import com.duxl.baselib.http.BaseHttpObserver;
import com.duxl.baselib.http.RetrofitManager;
import com.duxl.baselib.rx.LifecycleTransformer;
import com.duxl.baselib.utils.EmptyUtils;
import com.duxl.baselib.utils.NullUtils;
import com.duxl.baselib.utils.SPUtils;
import com.duxl.baselib.utils.Utils;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.hash.coinconvert.BuildConfig;
import com.hash.coinconvert.Constants;
import com.hash.coinconvert.R;
import com.hash.coinconvert.database.CurrencyDaoWrap;
import com.hash.coinconvert.databinding.ActivityMainBinding;
import com.hash.coinconvert.entity.CurrencyInfo;
import com.hash.coinconvert.entity.VersionInfo;
import com.hash.coinconvert.http.Root;
import com.hash.coinconvert.http.SocketSender;
import com.hash.coinconvert.http.api.VersionAPI;
import com.hash.coinconvert.livedatabus.LiveDataKey;
import com.hash.coinconvert.livedatabus.event.SymbolsRateEvent;
import com.hash.coinconvert.quotes.RateQuotesUtils;
import com.hash.coinconvert.ui.fragment.dialog.VersionDialog;
import com.hash.coinconvert.utils.Dispatch;
import com.hash.coinconvert.utils.LocationUtils;
import com.hash.coinconvert.widget.ConvertGroupView;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.zhangke.websocket.WebSocketHandler;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class MainActivity extends BaseActivity implements ConvertGroupView.OnConvertItemsAddedListener {

    private static final String TAG = "MainActivity";

    private ActivityMainBinding mBinding;
    private CurrencyDaoWrap mCurrencyDao;
    private String mFirstItemToken;
    private String mFirstItemType;
    private String mFirstItemIcon;
    private long mLastRefreshTime; // 上一次刷新UI时间
    private long mLastUpdateTime; // 上一次汇率更新时间

    private boolean mUiConfigChanged;
    private final int mRequestCodeForSetting = 1;

    @SuppressLint("NewApi")
    public ActivityResultLauncher<String[]> mPermissionMutLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), map -> {
        Boolean accessCoarseLocation = map.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
        Boolean accessFineLocation = map.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
        Log.i(TAG, "位置权限: accessCoarseLocation=" + accessCoarseLocation + ", accessFineLocation=" + accessFineLocation);
        if (accessCoarseLocation && accessFineLocation) {
            getCurrentCounty(true);
        } else {
            SPUtils.getInstance().put(Constants.SP.KEY.DEFAULT_LOCAL_CURRENCY, false);
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrencyDao = new CurrencyDaoWrap(getLifecycle());
        mUiConfigChanged = false;
        loadLatestVersion();
    }

    @Override
    public void onUiChanged(Configuration newConfig) {
        super.onUiChanged(newConfig);
        mUiConfigChanged = true;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @SuppressLint("NewApi")
    @Override
    public void onConvertItemsAdded() {
        Log.i(TAG, "onConvertItemsAdded");
        // 每次启动都更新下货币列表
        SocketSender.sendCurrencyTokensList();

        List<String> tokens = getHomeTokens();
        if (EmptyUtils.isEmpty(tokens)) {
            Log.i(TAG, "home token is empty");
            int rateItemCount = SPUtils.getInstance().getInt(Constants.SP.KEY.RATE_ITEM_COUNT, Constants.SP.DEFAULT.RATE_ITEM_COUNT);
            // 默认显示的token（BTC、USDT、DOGE、ETH+随机货币）
            List<String> defaultToken = Utils.asList(new String[]{"BTC", "USDT", "DOGE", "ETH"});
            List<CurrencyInfo> allCurrency = mCurrencyDao.getCurrencyAll();
            List<CurrencyInfo> defaultCurrency = allCurrency.stream().filter(it -> defaultToken.contains(it.token.toUpperCase())).collect(Collectors.toList());
            Collections.sort(defaultCurrency, Comparator.comparingInt(o -> defaultToken.indexOf(o.token)));
            int diffCount = defaultCurrency.size() - rateItemCount;
            if (diffCount > 0) {
                for (int i = 0; i < diffCount; i++) {
                    defaultCurrency.remove(defaultCurrency.size() - 1);
                }
            } else if (diffCount < 0) {
                Random random = new Random();
                for (int i = 0; i < -diffCount; i++) {
                    int index = random.nextInt(allCurrency.size());
                    CurrencyInfo currency = allCurrency.get(index);
                    if (defaultToken.contains(currency.token.toUpperCase())) {
                        i--;
                        continue;
                    }
                    defaultToken.add(currency.token.toUpperCase());
                    defaultCurrency.add(currency);
                }
            }
            String tokenJson = new Gson().toJson(defaultToken);
            Log.i(TAG, "save default token: " + tokenJson);
            SPUtils.getInstance().put(Constants.SP.KEY.HOME_TOKEN_LIST, tokenJson);

            mBinding.convertGroupView.setCurrencyData(defaultCurrency);
            mFirstItemToken = defaultCurrency.get(0).token;
            mFirstItemType = defaultCurrency.get(0).currencyType;
            mFirstItemIcon = defaultCurrency.get(0).icon;
            // 获取首页token
            String location = SPUtils.getInstance().getString(Constants.SP.KEY.LOCATION_COUNTRY_CODE);
            SocketSender.sendCurrentDefaultCurrency(rateItemCount, location);
        } else {
            // 更新当前列表汇率
            SocketSender.sendSymbolsRateRequest(String.join(",", tokens));
            showToken();
        }

        // 没有询问过定位，需要询问定位当前国家
        if (!SPUtils.getInstance().getBoolean(Constants.SP.KEY.LOCATION_ACCESS)) {
            SPUtils.getInstance().put(Constants.SP.KEY.LOCATION_ACCESS, true);
            if (hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION) && hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                getCurrentCounty(true);
            } else {
                mPermissionMutLauncher.launch(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION});
            }
        } else {
            // 询问过定位，有权限直接更新定位
            if (hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION) && hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                getCurrentCounty(false);
            }
        }

        Dispatch.I.postDelayed(mLoopGetRate, 1000);
    }

    @SuppressLint("NewApi")
    @Override
    protected void initView(View v) {
        super.initView(v);
        mBinding = ActivityMainBinding.bind(v);
        hideActionBar();
        mBinding.actionBarSetting.setOnClickListener(view -> {
            goActivity(SettingActivity.class, mRequestCodeForSetting);
            slideInBottom();
        });
        mBinding.numKeyboardView.setOnKeyInputListener(code -> mBinding.convertGroupView.onKeyInput(code));
        mBinding.numKeyboardView.setLifecycleOwner(this);
        mBinding.convertGroupView.setOnConvertItemsAddedListener(this);
        mBinding.convertGroupView.setLifecycleOwner(this);
        mBinding.convertGroupView.setOnSlideListener((position, orientation, currencyInfo) -> {
            if (orientation == -1) { // 向左滑动（切换汇率）
                goActivity(SwitchCurrencyActivity.class, 10 + position);
                slideInLeft();
            } else if (orientation == 1) { // 向右滑动
                Bundle args = new Bundle();
                Map<String, String> map = new HashMap<>();
                map.put("slideToken", currencyInfo.token);
                map.put("currencyType", currencyInfo.currencyType);
                map.put("slideIcon", currencyInfo.icon);
                map.put("firstToken", mFirstItemToken);
                map.put("firstType", mFirstItemType);
                map.put("firstIcon", mFirstItemIcon);
                args.putString("currencyInfo", RateQuotesUtils.transMapToString(map));
                goActivity(RateDetailActivity.class, args, 20 + position);
                slideInRight();
            }
        });
        LiveEventBus.get(LiveDataKey.HOME_TOKEN_LIST_CHANGED).observe(this, o -> showToken());
        LiveEventBus.get(LiveDataKey.CURRENCY_TOKENS_LIST_CHANGED).observe(this, o -> showToken());
        LiveEventBus.get(LiveDataKey.SYMBOLS_RATE_CHANGED, SymbolsRateEvent.class).observe(this, event -> {
            mLastUpdateTime = SystemClock.elapsedRealtime();
            String reduce = event.getList().stream().reduce("", (s, currencyInfo) -> s + currencyInfo.token + "," + currencyInfo.price + ", ", String::concat);
            Log.i(TAG, "汇率更新事件：" + reduce);
            long curTime = System.currentTimeMillis();
            Dispatch.I.removeUICallbacks(refreshTokenUIRunnable);
            if (curTime - mLastRefreshTime > Constants.RATE_UI_REFRESH) {
                // 刷新汇率UI
                refreshTokenUIRunnable.run();
            } else {
                // 延迟刷新UI
                long delayTime = Constants.RATE_UI_REFRESH - curTime + mLastRefreshTime;
                Dispatch.I.postUIDelayed(refreshTokenUIRunnable, delayTime);
            }
        });
        // 没有询问过定位，需要询问定位当前国家
        if (!SPUtils.getInstance().getBoolean(Constants.SP.KEY.LOCATION_ACCESS)) {
            SPUtils.getInstance().put(Constants.SP.KEY.LOCATION_ACCESS, true);
            if (hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION) && hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                getCurrentCounty(true);
            } else {
                mPermissionMutLauncher.launch(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION});
            }
        } else {
            // 询问过定位，有权限直接更新定位
            if (hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION) && hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                getCurrentCounty(false);
            }
        }
    }

    private Runnable refreshTokenUIRunnable = new Runnable() {
        @Override
        public void run() {
            if (isDestroyed() || isFinishing()) {
                return;
            }
            mLastRefreshTime = System.currentTimeMillis();
            showToken();
        }
    };

    @SuppressLint("NewApi")
    private void showToken() {
        List<String> tokens = getHomeTokens();
        if (EmptyUtils.isNotEmpty(tokens)) {
            List<CurrencyInfo> currencyTokens = mCurrencyDao.getCurrencyByTokens(tokens);
            if(EmptyUtils.isNotEmpty(currencyTokens)) {
                Collections.sort(currencyTokens, Comparator.comparingInt(o -> tokens.indexOf(o.token)));
                mBinding.convertGroupView.setCurrencyData(currencyTokens);
                mFirstItemToken = tokens.get(0);
                mFirstItemIcon = currencyTokens.get(0).icon;
                mFirstItemType = currencyTokens.get(0).currencyType;
            }
        }
    }

    /**
     * 获取当前国家
     *
     * @param getCurrency 定位成功后，是否需要获取当前定位的默认货币列表
     */
    private void getCurrentCounty(boolean getCurrency) {
        LocationUtils.getLocation(MainActivity.this, location -> {
            if ("locale".equals(location.getProvider())) {
                String countryCode = location.getExtras().getString("countryCode");
                SPUtils.getInstance().put(Constants.SP.KEY.LOCATION_COUNTRY_CODE, countryCode);
                if (getCurrency) {
                    int rateItemCount = SPUtils.getInstance().getInt(Constants.SP.KEY.RATE_ITEM_COUNT, Constants.SP.DEFAULT.RATE_ITEM_COUNT);
                    SocketSender.sendCurrentDefaultCurrency(rateItemCount, countryCode);
                }
                Log.i(TAG, "当前国家code：" + countryCode);
                return;
            }
            Log.i(TAG, "当前位置：longitude=" + location.getLongitude() + ", latitude=" + location.getLatitude());
            Address address = LocationUtils.getAddressFromLocation(location);
            if (address == null) {
                // try again get address
                Log.i(TAG, "address is null, try again get address");
                address = LocationUtils.getAddressFromLocation(location);
            }
            if (address != null) {
                Log.i(TAG, "当前国家：" + address.getCountryName() + ", " + address.getCountryCode());
                String countryCode = NullUtils.format(address.getCountryCode()).toString();
                SPUtils.getInstance().put(Constants.SP.KEY.LOCATION_COUNTRY_CODE, countryCode);
                if (getCurrency) {
                    int rateItemCount = SPUtils.getInstance().getInt(Constants.SP.KEY.RATE_ITEM_COUNT, Constants.SP.DEFAULT.RATE_ITEM_COUNT);
                    SocketSender.sendCurrentDefaultCurrency(rateItemCount, countryCode);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.convertGroupView.closeSwipe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mUiConfigChanged) {
            if (WebSocketHandler.getDefault().isConnect()) {
                //WebSocketHandler.getDefault().disConnect();
            }
            Dispatch.I.removeCallbacks(mLoopGetRate);
        }
    }

    private List<String> getHomeTokens() {
        String tokenJson = SPUtils.getInstance().getString(Constants.SP.KEY.HOME_TOKEN_LIST);
        if (EmptyUtils.isNotEmpty(tokenJson)) {
            List<String> tokens = new Gson().fromJson(tokenJson, new TypeToken<List<String>>() {
            }.getType());
            return tokens;
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, ActivityResult result) {
        super.onActivityResult(requestCode, result);
        int rateItemCount = SPUtils.getInstance().getInt(Constants.SP.KEY.RATE_ITEM_COUNT, Constants.SP.DEFAULT.RATE_ITEM_COUNT);
        if (requestCode >= 10 && requestCode <= 10 + rateItemCount) { // 切换货币返回
            int position = requestCode - 10;
            if (EmptyUtils.isNotNull(result) && EmptyUtils.isNotNull(result.getData())) {
                CurrencyInfo currencyInfo = result.getData().getParcelableExtra("data");
                // 更新首页列表
                List<String> tokens = getHomeTokens();
                if (EmptyUtils.isNotEmpty(tokens)) {
                    tokens.remove(position);
                    tokens.add(position, currencyInfo.token);
                    SPUtils.getInstance().put(Constants.SP.KEY.HOME_TOKEN_LIST, new Gson().toJson(tokens));
                    showToken();

                    // 更新币对汇率
                    SocketSender.sendSymbolsRateRequest(String.join(",", tokens));
                }
            }
        } else if (requestCode == 1) {
            if (EmptyUtils.isNotNull(result) && EmptyUtils.isNotNull(result.getData())) {
                // 设置页面开启了定位
                boolean localCurrency = result.getData().getBooleanExtra("local_currency", false);
                if (localCurrency) {
                    getCurrentCounty(true);
                }
            }
        }
    }

    private void loadLatestVersion() {
        RetrofitManager
                .getInstance()
                .create(VersionAPI.class)
                .get()
                .compose(new LifecycleTransformer<Root<VersionInfo>>(this))
                .subscribe(new BaseHttpObserver<Root<VersionInfo>>() {
                    @Override
                    public void onSuccess(Root<VersionInfo> root) {
                        // has new version
                        if (root.data != null && root.data.versionCode > BuildConfig.VERSION_CODE) {
                            showUpgradeDialog(root.data);
                        }
                    }
                });
    }

    private void showUpgradeDialog(VersionInfo versionInfo) {
        VersionDialog
                .newInstance(versionInfo)
                .showDialog(getSupportFragmentManager());
    }

    private Runnable mLoopGetRate = new Runnable() {
        @Override
        public void run() {
            Dispatch.I.removeCallbacks(this);
            long duration = SystemClock.elapsedRealtime() - mLastUpdateTime;
            if (duration > 1000) {
                // 更新当前列表汇率
                if (WebSocketHandler.getDefault().isConnect()) {
                    List<String> tokens = getHomeTokens();
                    Log.i(TAG, "rate delay, try get: tokens=" + tokens);
                    if (EmptyUtils.isNotEmpty(tokens)) {
                        SocketSender.sendSymbolsRateRequest(String.join(",", tokens));
                    }
                }
            }
            Dispatch.I.postUIDelayed(mLoopGetRate, duration > 3000 ? 500 : 1000);
        }
    };
}
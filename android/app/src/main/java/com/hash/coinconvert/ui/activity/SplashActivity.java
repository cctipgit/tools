package com.hash.coinconvert.ui.activity;

import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import com.duxl.baselib.http.BaseHttpObserver;
import com.duxl.baselib.http.RetrofitManager;
import com.duxl.baselib.rx.LifecycleTransformer;
import com.hash.coinconvert.BuildConfig;
import com.hash.coinconvert.R;
import com.hash.coinconvert.entity.VersionInfo;
import com.hash.coinconvert.http.Root;
import com.hash.coinconvert.http.api.VersionAPI;
import com.hash.coinconvert.ui.fragment.dialog.VersionDialog;
import com.hash.coinconvert.utils.Dispatch;
import com.zhangke.websocket.WebSocketHandler;

public class SplashActivity extends BaseActivity {

    private final String TAG = "SplashActivity";
    private long mStartTime; // page start time
    private final long mPageDuration = 500; // page duration

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView(View v) {
        super.initView(v);
        hideActionBar();
        hideStateBar();

        mStartTime = SystemClock.elapsedRealtime();
        toNextPage();
    }

    private void toNextPage() {
        long pageDuration = Math.min(SystemClock.elapsedRealtime() - mStartTime, mPageDuration);
        Dispatch.I.postUIDelayed(() -> {
            goActivity(MainActivity.class);
            finish();
        }, mPageDuration - pageDuration);
    }
}
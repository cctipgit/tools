package com.hash.coinconvert.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.facebook.react.ReactPackage;
import com.hash.coinconvert.App;
import com.hash.coinconvert.MainActivity;
import com.hash.coinconvert.R;
import com.hash.coinconvert.livedatabus.event.AppsFlyerEvent;
import com.hash.coinconvert.rnmodule.ToolModulePackage;
import com.hash.coinconvert.ui2.activity.HomeActivity;
import com.hash.coinconvert.utils.Dispatch;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.jeremyliao.liveeventbus.core.Observable;

import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private final String TAG = "SplashActivity";
    private long mStartTime; // page start time
    private final long mPageDuration = 1200; // page duration
    private Handler timeoutHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mStartTime = SystemClock.elapsedRealtime();
        Observable<AppsFlyerEvent> observable = LiveEventBus.get(AppsFlyerEvent.KEY);
        observable.observe(this, new Observer<AppsFlyerEvent>() {
            @Override
            public void onChanged(AppsFlyerEvent event) {
                if (event.success) {
                    App app = (App) getApplicationContext();
                    List<ReactPackage> list = app.getReactNativeHost().getReactInstanceManager().getPackages();
                    for (ReactPackage item : list) {
                        if (item instanceof ToolModulePackage) {
                            ((ToolModulePackage) item).appsFlyerConversation = event.map;
                            toNextPage(HomeActivity.class);
                            break;
                        }
                    }
                } else {
                    toNextPage(HomeActivity.class);
                }
                observable.removeObserver(this);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeoutHandler.removeCallbacksAndMessages(null);
        timeoutHandler = null;
    }

    private void toNextPage(Class<? extends Activity> clz) {
        long pageDuration = Math.min(SystemClock.elapsedRealtime() - mStartTime, mPageDuration);
        Dispatch.I.postUIDelayed(() -> {
            Intent intent = new Intent(this, clz);
            intent.putExtra("from_splash", true);
            startActivity(intent);
            finish();
        }, mPageDuration - pageDuration);
    }
}
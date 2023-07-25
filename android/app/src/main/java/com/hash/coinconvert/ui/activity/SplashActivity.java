package com.hash.coinconvert.ui.activity;

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
import com.hash.coinconvert.utils.Dispatch;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.jeremyliao.liveeventbus.core.Observable;

import java.util.List;


public class SplashActivity extends AppCompatActivity {

    private final String TAG = "SplashActivity";
    private long mStartTime; // page start time
    private final long mPageDuration = 800; // page duration
    private long timeout = 8000L;
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
                App app = (App) getApplicationContext();
                List<ReactPackage> list = app.getReactNativeHost().getReactInstanceManager().getPackages();
                for (ReactPackage item : list) {
                    if (item instanceof ToolModulePackage) {
                        ((ToolModulePackage) item).appsFlyerConversation = event.map;
                        toNextPage();
                        break;
                    }
                }
                observable.removeObserver(this);
            }
        });
        //if appsflyer do not invoke the conversation callback. start native page
//        timeoutHandler.postDelayed(() -> {
//            startActivity(new Intent(this, HomeActivity.class));
//            finish();
//        }, timeout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeoutHandler.removeCallbacksAndMessages(null);
        timeoutHandler = null;
    }

    private void toNextPage() {
        long pageDuration = Math.min(SystemClock.elapsedRealtime() - mStartTime, mPageDuration);
        Dispatch.I.postUIDelayed(() -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }, mPageDuration - pageDuration);
    }
}
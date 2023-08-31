package com.rate.quiz.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.facebook.react.ReactPackage;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.jeremyliao.liveeventbus.core.Observable;
import com.rate.quiz.App;
import com.rate.quiz.MainActivity;
import com.rate.quiz.R;
import com.rate.quiz.livedatabus.event.AppsFlyerEvent;
import com.rate.quiz.rnmodule.ToolModulePackage;
import com.rate.quiz.ui2.activity.HomeActivity;
import com.rate.quiz.utils.Dispatch;
import com.reactnativecommunity.asyncstorage.AsyncLocalStorageUtil;
import com.reactnativecommunity.asyncstorage.ReactDatabaseSupplier;

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
        getFromAsyncLocalStorage();
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

    private void getFromAsyncLocalStorage() {
        new Thread(() -> {
            ReactDatabaseSupplier supplier = ReactDatabaseSupplier.getInstance(getApplicationContext());
            String value = AsyncLocalStorageUtil.getItemImpl(supplier.getReadableDatabase(), "from");
            supplier.closeDatabase();
            if ("true".equals(value)) {
                runOnUiThread(() -> toNextPage(MainActivity.class));
            }
        }).start();
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
package com.hash.coinconvert.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hash.coinconvert.MainActivity;
import com.hash.coinconvert.R;
import com.hash.coinconvert.ui2.activity.HomeActivity;
import com.hash.coinconvert.utils.Dispatch;

public class SplashActivity extends AppCompatActivity {

    private final String TAG = "SplashActivity";
    private long mStartTime; // page start time
    private final long mPageDuration = 800; // page duration

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mStartTime = SystemClock.elapsedRealtime();
        toNextPage();
    }

    private void toNextPage() {
        long pageDuration = Math.min(SystemClock.elapsedRealtime() - mStartTime, mPageDuration);
        Dispatch.I.postUIDelayed(() -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }, mPageDuration - pageDuration);
    }
}
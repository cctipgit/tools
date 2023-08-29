package com.rate.quiz;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint;
import com.facebook.react.defaults.DefaultReactActivityDelegate;
import com.rate.quiz.rnmodule.view.MultiWebView;
import com.rate.quiz.ui2.activity.HomeActivity;

public class MainActivity extends ReactActivity {

    /**
     * Returns the name of the main component registered from JavaScript. This is used to schedule
     * rendering of the component.
     */
    @Override
    protected String getMainComponentName() {
        return "MyApp";
    }

    /**
     * Returns the instance of the {@link ReactActivityDelegate}. Here we use a util class {@link
     * DefaultReactActivityDelegate} which allows you to easily enable Fabric and Concurrent React
     * (aka React 18) with two boolean flags.
     */
    @Override
    protected ReactActivityDelegate createReactActivityDelegate() {
        return new DefaultReactActivityDelegate(
                this,
                getMainComponentName(),
                // If you opted-in for the New Architecture, we enable the Fabric Renderer.
                DefaultNewArchitectureEntryPoint.getFabricEnabled());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isNightMode()) {
            setStateBarLightMode();
        } else {
            setStateBarDarkMode();
        }
        if (!getIntent().getBooleanExtra("from_splash", false)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (!intent.getBooleanExtra("from_splash", false)) {
//            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }

    /**
     * 是否是深色模式
     *
     * @return
     */
    protected boolean isNightMode() {
        int uiMode = getResources().getConfiguration().uiMode;
        return (uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    /**
     * 设置状态栏字体和图标为深色
     * Android版本在6.0以上时可以调用此方法来改变状态栏字体图标颜色
     */
    public void setStateBarDarkMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置状态栏字体和图标为浅色
     */
    public void setStateBarLightMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        View v = getWindow().getDecorView().getRootView().findViewWithTag(MultiWebView.TAG);
        if (v instanceof MultiWebView) {
            if (((MultiWebView) v).goBack()) {
                return;
            }
        }
        super.onBackPressed();
    }
}

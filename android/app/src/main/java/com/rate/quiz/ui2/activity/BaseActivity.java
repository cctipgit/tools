package com.rate.quiz.ui2.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.gw.swipeback.SwipeBackLayout;
import com.rate.quiz.R;
import com.rate.quiz.widget.OnConfigurationChangedListener;

public abstract class BaseActivity extends AppCompatActivity implements OnConfigurationChangedListener {

    private final String TAG = "BaseActivity";
    private ActivityResultLauncher<Intent> mIntentActivityResultLauncher;
    public static final String KEY_INTERNAL_REQUEST_CODE = "internalRequestCode";
    private int maskAlpha = 20;
    private SwipeBackLayout mSwipeBackLayout;
    private boolean mSwipeSlideStarted;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIntentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            int requestCode = 0;
            if (result != null && result.getData() != null) {
                requestCode = result.getData().getIntExtra(KEY_INTERNAL_REQUEST_CODE, 0);
            }
            onActivityResult(requestCode, result);
        });
        if (!isNightMode()) {
            setStateBarLightMode();
        } else {
            setStateBarDarkMode();
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

    @Override
    public void onUiChanged(Configuration newConfig) {

    }

    protected void goActivity(Class clsActivity) {
        goActivity(clsActivity, null);
    }

    protected void goActivity(Class clsActivity, Bundle extras) {
        Intent intent = new Intent(this, clsActivity);
        if (extras != null) {
            intent.putExtras(extras);
        }
        startActivity(intent);
    }

    protected void goActivity(Class clsActivity, int requestCode) {
        goActivity(clsActivity, null, requestCode);
    }

    protected void goActivity(Class clsActivity, Bundle extras, int requestCode) {
        Intent intent = new Intent(this, clsActivity);
        intent.putExtra(KEY_INTERNAL_REQUEST_CODE, requestCode);
        if (extras != null) {
            intent.putExtras(extras);
        }
        mIntentActivityResultLauncher.launch(intent);
    }

    protected void setResult2(int resultCode) {
        setResult2(resultCode, null);
    }

    protected void setResult2(int resultCode, Intent data) {
        if (data == null) {
            data = new Intent();
        }
        data.putExtra(KEY_INTERNAL_REQUEST_CODE, getIntent().getIntExtra(KEY_INTERNAL_REQUEST_CODE, 0));
        super.setResult(resultCode, data);
    }


    protected void onActivityResult(int requestCode, ActivityResult result) {

    }

    /**
     * 设置支持滑动关闭
     *
     * @param direction 滑动方向：
     *                  上{@link SwipeBackLayout#FROM_TOP}
     *                  下{@link SwipeBackLayout#FROM_BOTTOM}
     *                  左{@link SwipeBackLayout#FROM_LEFT}
     *                  右{@link SwipeBackLayout#FROM_RIGHT}
     */
    protected void setSlideEnable(int direction) {
        mSwipeBackLayout = new SwipeBackLayout(this);
        //WxSwipeBackLayout swipeBackLayout = new WxSwipeBackLayout(this);
        mSwipeBackLayout.attachToActivity(this);
        mSwipeBackLayout.setDirectionMode(direction);
        //swipeBackLayout.setSwipeFromEdge(true); // 设置是否仅可以从边缘滑动
        mSwipeBackLayout.setMaskAlpha(maskAlpha); // 设置开始滑动时蒙层的透明度
        mSwipeBackLayout.setSwipeBackFactor(1); // 设置滑动因子
        mSwipeBackLayout.setSwipeBackListener(new SwipeBackLayout.OnSwipeBackListener() {
            @Override
            public void onViewPositionChanged(View mView, float swipeBackFraction, float swipeBackFactor) {
                if (!mSwipeSlideStarted && swipeBackFraction > 0) {
                    //Log.i(TAG, "onViewPositionChanged: swipeBackFraction=" + swipeBackFraction + ", swipeBackFactor=" + swipeBackFactor);
                    mSwipeSlideStarted = true;
                }
            }

            @Override
            public void onViewSwipeFinished(View mView, boolean isEnd) {
                //Log.i(TAG, "onViewSwipeFinished: isEnd=" + isEnd);
                mSwipeSlideStarted = false;
                if (isEnd) {
                    finish();
                    slideOutNone();
                }
            }
        });
    }


    protected void slideInLeft() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_none);
    }

    protected void slideInRight() {
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_none);
    }

    protected void slideInBottom() {
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_none);
    }

    protected void slideOutLeft() {
        overridePendingTransition(R.anim.slide_none, R.anim.slide_out_left);
    }

    protected void slideOutRight() {
        overridePendingTransition(R.anim.slide_none, R.anim.slide_out_right);
    }

    protected void slideOutBottom() {
        overridePendingTransition(R.anim.slide_none, R.anim.slide_out_bottom);
    }

    protected void slideOutNone() {
        overridePendingTransition(0, 0);
    }

    protected boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission)
                == PackageManager.PERMISSION_GRANTED;
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
}

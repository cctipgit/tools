package com.hash.coinconvert.ui.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.duxl.baselib.ui.status.IStatusView;
import com.duxl.baselib.utils.DisplayUtil;
import com.duxl.baselib.widget.SmartRecyclerView;
import com.flyco.roundview.RoundTextView;
import com.gw.swipeback.SwipeBackLayout;
import com.hash.coinconvert.R;
import com.hash.coinconvert.livedatabus.LiveDataKey;
import com.hash.coinconvert.livedatabus.event.PageSlideEvent;
import com.hash.coinconvert.widget.AppStatusView;
import com.hash.coinconvert.widget.OnConfigurationChangedListener;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.zhangke.websocket.WebSocketHandler;

public abstract class BaseActivity extends com.duxl.baselib.ui.activity.BaseActivity implements OnConfigurationChangedListener {

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
    }

    @Override
    protected void initView(View v) {
        super.initView(v);
        if (isNightMode()) {
            setStateBarLightMode();
        } else {
            setStateBarDarkMode();
        }
    }

    @Override
    public void onBackPressed() {
        onClickActionBack(findViewById(com.duxl.baselib.R.id.iv_action_back));
    }

    @Override
    protected IStatusView initStatusView() {
        return new AppStatusView(this, this);
    }

    /**
     * 重设RecyclerView的StatusView
     *
     * @param recyclerView
     */
    protected AppStatusView resetRecyclerStatusView(SmartRecyclerView recyclerView) {
        AppStatusView statusView = new AppStatusView(this, recyclerView);
        recyclerView.setStatusView(statusView);
        return statusView;
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

    @Override
    protected void onResume() {
        super.onResume();
        if (!WebSocketHandler.getDefault().isConnect()) {
            Log.i(TAG, "尝试重连");
            WebSocketHandler.getDefault().reconnect();
        }
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
     * 设置是否监听其他滑动页面并同步处理当前页面（达到同步滑动关闭效果）
     */
    protected void setObserveOtherSlideFinish() {
        LiveEventBus.get(LiveDataKey.DIALOG_PAGE_SLIDE, PageSlideEvent.class).observe(this, pageSlideEvent -> {
            if (pageSlideEvent.object != getClass()) {
                //Log.i(TAG, "当前页面=" + getClass().getSimpleName() + "，发生事件页面=" + pageSlideEvent.object.getSimpleName() + ", type=" + pageSlideEvent.type);
                if (pageSlideEvent.type == 0) {
                    mRootContentView.setVisibility(View.GONE);
                } else if (pageSlideEvent.type == 1) {
                    mRootContentView.setVisibility(View.VISIBLE);
                } else {
                    finish();
                    slideOutNone();
                }
            }
        });
    }

    /**
     * 设置Activity为Dialog样式
     */
    protected void setDialogStyle() {
        hideStateBar();
        mRootContentView.setBackgroundResource(R.drawable.dialog_activity_bg);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mRootContentView.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        }
        layoutParams.topMargin = DisplayUtil.dip2px(this, 30);
        mRootContentView.setLayoutParams(layoutParams);

        mActionBarView.getRlBar().setBackgroundColor(Color.TRANSPARENT);
        LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) mActionBarView.getLayoutParams();
        layoutParams1.topMargin = DisplayUtil.dip2px(this, 4);
        layoutParams1.leftMargin = layoutParams.rightMargin = DisplayUtil.dip2px(this, 4);
        mActionBarView.getRlBar().setLayoutParams(layoutParams1);
    }

    /**
     protected void slideTop() {
     ActivitySlidingBackConsumer activitySlidingBackConsumer = new ActivitySlidingBackConsumer(this);
     activitySlidingBackConsumer.setScrimColor(Color.TRANSPARENT);
     activitySlidingBackConsumer.setShadowColor(Color.TRANSPARENT);
     activitySlidingBackConsumer.addListener(new SwipeListener() {
    @Override public void onConsumerAttachedToWrapper(SmartSwipeWrapper wrapper, SwipeConsumer consumer) {
    Log.d("onConsumerAttachedToWrapper");
    }

    @Override public void onConsumerDetachedFromWrapper(SmartSwipeWrapper wrapper, SwipeConsumer consumer) {
    Log.d("onConsumerDetachedFromWrapper");
    }

    @Override public void onSwipeStateChanged(SmartSwipeWrapper wrapper, SwipeConsumer consumer, int state, int direction, float progress) {
    Log.d("onSwipeStateChanged: state=" + state + ", direction=" + direction + ", progress=" + progress);
    }

    @Override public void onSwipeStart(SmartSwipeWrapper wrapper, SwipeConsumer consumer, int direction) {
    Log.d("onSwipeStart: direction=" + direction);
    }

    @Override public void onSwipeProcess(SmartSwipeWrapper wrapper, SwipeConsumer consumer, int direction, boolean settling, float progress) {
    Log.d("onSwipeProcess: direction = " + direction + ", settling = " + settling + ", progress = " + progress);
    }

    @Override public void onSwipeRelease(SmartSwipeWrapper wrapper, SwipeConsumer consumer, int direction, float progress, float xVelocity, float yVelocity) {
    Log.d("onSwipeRelease: direction = " + direction + ", progress = " + progress + ", xVelocity = " + xVelocity + ", yVelocity = " + yVelocity);
    }

    @Override public void onSwipeOpened(SmartSwipeWrapper wrapper, SwipeConsumer consumer, int direction) {
    Log.d("onSwipeOpened: direction=" + direction);
    finish();
    overridePendingTransition(0, 0);
    }

    @Override public void onSwipeClosed(SmartSwipeWrapper wrapper, SwipeConsumer consumer, int direction) {
    Log.d("onSwipeClosed: direction=" + direction);
    }
    });
     activitySlidingBackConsumer.addListener(new SimpleSwipeListener(){
    @Override public void onSwipeOpened(SmartSwipeWrapper wrapper, SwipeConsumer consumer, int direction) {
    super.onSwipeOpened(wrapper, consumer, direction);
    finish();
    overridePendingTransition(0, 0);
    }
    });
     SmartSwipeWrapper wrap = SmartSwipe.wrap(this);
     wrap.setBackgroundColor(Color.TRANSPARENT);
     Log.i("duxl.log", "wrapper.cls=" + wrap.getClass().getName());
     wrap
     .addConsumer(activitySlidingBackConsumer)
     .setRelativeMoveFactor(1F)
     .enableDirection(SwipeConsumer.DIRECTION_TOP);
     }*/

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
                    LiveEventBus.get(LiveDataKey.DIALOG_PAGE_SLIDE).post(new PageSlideEvent(0, BaseActivity.this.getClass()));
                }
            }

            @Override
            public void onViewSwipeFinished(View mView, boolean isEnd) {
                //Log.i(TAG, "onViewSwipeFinished: isEnd=" + isEnd);
                mSwipeSlideStarted = false;
                LiveEventBus.get(LiveDataKey.DIALOG_PAGE_SLIDE).post(new PageSlideEvent(isEnd ? 2 : 1, BaseActivity.this.getClass()));
                if (isEnd) {
                    finish();
                    slideOutNone();
                }
            }
        });

        addIOSCloseBar();
    }

    // 添加ios关闭bar（添加顶部中间一个黑色横条）
    private void addIOSCloseBar() {
        RelativeLayout rlBar = mActionBarView.getRlBar();
        RoundTextView roundTextView = new RoundTextView(this);
        roundTextView.getDelegate().setBackgroundColor(getResources().getColor(R.color.theme_text_color));
        roundTextView.getDelegate().setIsRadiusHalfHeight(true);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(DisplayUtil.dip2px(this, 50), DisplayUtil.dip2px(this, 4));
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        roundTextView.setLayoutParams(layoutParams);
        rlBar.addView(roundTextView);
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
}

package com.hash.coinconvert.widget.lottery;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;

import com.duxl.baselib.utils.DisplayUtil;
import com.duxl.baselib.utils.ToastUtils;
import com.hash.coinconvert.R;
import com.hash.coinconvert.entity.PinItem;

import java.util.Arrays;
import java.util.List;

public class LotteryView extends View {
    public static final String TAG = "LotteryView";

    private int lotteryTop;

    private BackgroundComponent backgroundComponent;
    private PointsComponent pointsComponent;
    private AnchorComponent anchorComponent;

    private ValueAnimator rotateAnimation;
    private float rotateAngle;

    private OnRotateFinishListener onRewardListener;
    private String targetRewardId;

    public LotteryView(Context context) {
        super(context);
        init(context);
    }

    public LotteryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LotteryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        lotteryTop = DisplayUtil.dip2px(context, 18f);
        backgroundComponent = new BackgroundComponent(context);
        pointsComponent = new PointsComponent(context, null);
        anchorComponent = new AnchorComponent(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        backgroundComponent.onDraw(this, canvas, rotateAngle);
        pointsComponent.onDraw(this, canvas, rotateAngle);
        anchorComponent.onDraw(this, canvas, rotateAngle);
    }

    public void setOnRewardListener(OnRotateFinishListener onRewardListener) {
        this.onRewardListener = onRewardListener;
    }

    public void startRotate(String targetRewardId) {
        if (pointsComponent.findItemById(targetRewardId) == null) {
            ToastUtils.show(R.string.dialog_reward_error_none_pin_item);
            return;
        }
        this.targetRewardId = targetRewardId;
        rotateTo(pointsComponent.getTargetRewardRotateAngleInArc(targetRewardId, rotateAngle));
    }

    private void ensureAnimation() {
        if (rotateAnimation == null) {
            rotateAnimation = ObjectAnimator.ofFloat((float) 0, (float) (2 * Math.PI));
            rotateAnimation.setDuration(8000);
            rotateAnimation.addUpdateListener(animation -> {
                rotateAngle = (float) animation.getAnimatedValue();
                Log.d(TAG, "value:" + rotateAngle);
                invalidate();
            });
            rotateAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (onRewardListener != null) {
                        PinItem item = pointsComponent.findItemById(targetRewardId);
                        targetRewardId = null;
                        onRewardListener.onRotateFinished(item);
                    }
                }
            });
//            rotateAnimation.setInterpolator(new CustomInterpolator());
            rotateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        }
        if (rotateAnimation.isRunning()) {
            rotateAnimation.pause();
        }
    }

    public void setData(PinItem[] data) {
        pointsComponent.setData(Arrays.copyOf(data, data.length));
        invalidate();
    }

    private void rotateTo(double dest) {
        ensureAnimation();
        //避免无数次后数字大于Float.MAX
        float M = Component.CIRCLE_DEGREE * 10;
        if (rotateAngle > M) {
            rotateAngle -= M;
            dest -= M;
        }
        rotateAnimation.setFloatValues(rotateAngle, (float) dest);

        rotateAnimation.start();
    }

    public boolean isRunning() {
        return rotateAnimation != null && rotateAnimation.isRunning();
    }

    public void reset() {
        rotateAngle = 0;
        invalidate();
    }

    private void stopRotate() {
        if (rotateAnimation != null && rotateAnimation.isRunning()) {
            rotateAnimation.cancel();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.onRewardListener = null;
        stopRotate();
    }

    protected int getLotteryTop() {
        return lotteryTop;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, width + lotteryTop);
    }

    private static class CustomInterpolator implements TimeInterpolator {

        @Override
        public float getInterpolation(float input) {
            Log.d("CustomInterpolator", "" + input);
            return (float) (Math.cos((input + 1) * Math.PI) / 2.0f) + 0.5f;
        }
    }

    public interface OnRotateFinishListener {
        void onRotateFinished(PinItem item);
    }
}

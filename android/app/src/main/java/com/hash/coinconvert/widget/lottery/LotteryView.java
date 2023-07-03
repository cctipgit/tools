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
import com.hash.coinconvert.entity.PinItem;

import java.util.ArrayList;
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

    private List<PinItem> data;

    private void init(Context context) {
        lotteryTop = DisplayUtil.dip2px(context, 18f);
        backgroundComponent = new BackgroundComponent(context);

        this.data = new ArrayList<>();
        this.data.add(new PinItem("1", "30PTS"));
        this.data.add(new PinItem("2", "35PTS"));
        this.data.add(new PinItem("3", "10PTS"));
        this.data.add(new PinItem("4", "50PTS"));
        this.data.add(new PinItem("5", "20PTS"));
        this.data.add(new PinItem("6", "20PTS"));
        this.data.add(new PinItem("7", "5PTS"));
        this.data.add(new PinItem("8", "25PTS"));

        pointsComponent = new PointsComponent(context, data);
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
        rotateTo(pointsComponent.getTargetRewardRotateAngleInArc(targetRewardId, rotateAngle));
    }

    public void test() {
        int index = (int) (Math.random() * 100 % data.size());
        startRotate(data.get(index).id);
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
                        onRewardListener.onRotateFinished();
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

    private void rotateTo(double dest) {
        ensureAnimation();
        //避免无数次后数字大于Float.MAX
        float M = Component.CIRCLE_DEGREE * 10;
        if (rotateAngle > M) {
            rotateAngle -= M;
            dest -= M;
        }
        Log.d(PointsComponent.TAG, rotateAngle + "," + dest);
        float dif = (float) (dest - rotateAngle);
//        rotateAnimation.setFloatValues(rotateAngle,
//                rotateAngle + dif * 0.03125f,
//                rotateAngle + dif * 0.0625f,
//                rotateAngle + dif * 0.125f,
//                rotateAngle + dif * 0.25f,
//                rotateAngle + dif * 0.5f,
//                rotateAngle + dif);
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
        void onRotateFinished();
    }
}

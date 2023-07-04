package com.hash.coinconvert.widget.lottery;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.duxl.baselib.utils.DisplayUtil;
import com.hash.coinconvert.R;
import com.hash.coinconvert.entity.PinItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PointsComponent extends Component {
    public static final String TAG = "PointsComponent";
    public static final float DEGREE_CIRCLE = 360f;
    /**
     * start draw angle
     */
    public static final float START_CENTER_ANGLE = -90f;
    public static final int MIN_ROTATE_TIMES = 5;

    private PinItem[] data;

    private LinearGradient linear1;
    private LinearGradient linear2;
    private Paint paint;
    private TextPaint textPaint;
    private int strokeColor;
    private float arcWidth;

    private RectF rectF;
    private RectF textRectF;
    private Path textPath;
    private float rewardImageRadiusRate = 4f / 7f;

    public static final int[] rewardImageArray = new int[]{
            R.mipmap.ic_lottery_reward_1,
            R.mipmap.ic_lottery_reward_2,
            R.mipmap.ic_lottery_reward_3,
            R.mipmap.ic_lottery_reward_4,
            R.mipmap.ic_lottery_reward_5,
            R.mipmap.ic_lottery_reward_6,
            R.mipmap.ic_lottery_reward_7,
            R.mipmap.ic_lottery_reward_8,
    };

    private List<Drawable> rewardDrawableList;
    private int rewardPicSize;

    public PointsComponent(Context context, PinItem[] data) {
        this.data = data;
        if (this.data == null) {
            this.data = new PinItem[0];
        }
        arcWidth = DisplayUtil.dip2px(context, 34f);
        strokeColor = Color.parseColor("#B671FF");
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(DisplayUtil.dip2px(context, 4));

        textPaint = new TextPaint();
        textPaint.setTypeface(ResourcesCompat.getFont(context, R.font.monomaniacone_regular));
        textPaint.setColor(ContextCompat.getColor(context, R.color.theme_bg));
        textPaint.setTextSize(DisplayUtil.sp2px(context, 20));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(false);
        textPath = new Path();

        rewardPicSize = DisplayUtil.dip2px(context, 50);
        rewardDrawableList = new ArrayList<>();
        for (int id : rewardImageArray) {
            rewardDrawableList.add(ContextCompat.getDrawable(context, id));
        }
        for (int i = 0; i < this.data.length; i++) {
            this.data[i].picResId = rewardImageArray[i%this.data.length];
        }
    }

    public void setData(PinItem[] data) {
        this.data = data;
        for (int i = 0; i < this.data.length; i++) {
            this.data[i].picResId = rewardImageArray[i%this.data.length];
        }
    }

    @Override
    public void onDraw(LotteryView view, Canvas canvas, float rotateAngle) {
        if (data.length == 0) return;
        int w = view.getWidth();
        int h = view.getHeight();
        int cx = w / 2;
        int cy = view.getLotteryTop() + cx;
        int radius = (int) (cx - arcWidth);
        if (rectF == null) {
            rectF = new RectF(arcWidth, view.getLotteryTop() + arcWidth, w - arcWidth, h - arcWidth);
        }

        ensureLinearGradients(w, w);
        float itemArcAngle = DEGREE_CIRCLE / data.length;
        float startDrawAngle = START_CENTER_ANGLE - itemArcAngle / 2 + rotateAngle;
        Paint.FontMetrics fm = textPaint.getFontMetrics();

        int radiusOfTextCircle = (int) (radius - textPaint.getTextSize() * 1.5f);
        int radiusOfRewardPic = (int) (radius * rewardImageRadiusRate);
        if (textRectF == null) {
            textRectF = new RectF(cx - radiusOfTextCircle, cy - radiusOfTextCircle, cx + radiusOfTextCircle, cy + radiusOfTextCircle);
        }

        int halfRewardPicSize = rewardPicSize / 2;
        for (int i = 0; i < data.length; i++) {
            //gradient
            paint.setShader((i & 1) == 0 ? linear1 : linear2);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawArc(rectF, startDrawAngle, itemArcAngle, true, paint);

            //border
            paint.setShader(null);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(strokeColor);
            canvas.drawArc(rectF, startDrawAngle, itemArcAngle, true, paint);

            //text
            textPath.reset();
            textPath.addArc(textRectF, startDrawAngle, itemArcAngle);
            canvas.drawTextOnPath(data[i].desc, textPath, 0, 0, textPaint);

            //pic
            canvas.save();
            float picAngle = startDrawAngle + itemArcAngle / 2;
            double picAngleInArc = picAngle * Math.PI * 2 / DEGREE_CIRCLE;
            int picCX = (int) getArcX(cx, picAngleInArc, radiusOfRewardPic);
            int picCY = (int) getArcY(cy, picAngleInArc, radiusOfRewardPic);
            canvas.rotate(i * itemArcAngle + rotateAngle, picCX, picCY);
            Drawable drawable = getRewardDrawable(i);
            drawable.setBounds(picCX - halfRewardPicSize, picCY - halfRewardPicSize,
                    picCX + halfRewardPicSize, picCY + halfRewardPicSize);
            drawable.draw(canvas);
            canvas.rotate(0, picCX, picCY);
            canvas.restore();

            startDrawAngle += itemArcAngle;
        }
    }

    private Drawable getRewardDrawable(int index) {
        return rewardDrawableList.get(index % rewardDrawableList.size());
    }

    private void ensureLinearGradients(int w, int h) {
        //#AE94FF, #7747F2
        if (linear1 == null) {
            linear1 = new LinearGradient(0, 0, w, h, new int[]{
                    Color.parseColor("#AE94FF"),
                    Color.parseColor("#7747F2")
            }, null, Shader.TileMode.CLAMP);
        }
        //#94D1FF, #4598F2
        if (linear2 == null) {
            linear2 = new LinearGradient(0, 0, w, h, new int[]{
                    Color.parseColor("#94D1FF"),
                    Color.parseColor("#4598F2")
            }, null, Shader.TileMode.CLAMP);
        }
    }

    public PinItem findItemById(String id) {
        for (PinItem item : data) {
            if (Objects.equals(item.id, id)) {
                return item;
            }
        }
        return null;
    }

    private double getArcX(int cx, double angle, int radius) {
        return cx + radius * Math.cos(angle);
    }

    private double getArcY(int cy, double angle, int radius) {
        return cy + radius * Math.sin(angle);
    }

    public float getTargetRewardRotateAngleInArc(String id, float from) {
        float itemArcAngle = DEGREE_CIRCLE / data.length;
        float origin = START_CENTER_ANGLE - itemArcAngle / 2;

        for (int i = 0; i < data.length; i++) {
            PinItem item = data[i];
            if (Objects.equals(id, item.id)) {
                float start = i * itemArcAngle - itemArcAngle / 2;
                float end = start + itemArcAngle;
                float random = (float) (start + (end - start) * Math.random());
                random = DEGREE_CIRCLE - random;
                while (random < from + DEGREE_CIRCLE * MIN_ROTATE_TIMES) {
                    random += DEGREE_CIRCLE;
                }
                return random;
            }
        }
        return origin;
    }
}

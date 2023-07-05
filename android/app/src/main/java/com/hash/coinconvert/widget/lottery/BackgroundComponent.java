package com.hash.coinconvert.widget.lottery;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.duxl.baselib.utils.DisplayUtil;

public class BackgroundComponent extends Component {

    public static final String TAG = "BackgroundComponent";

    private int backgroundColor;
    private int dotColor;
    private int arcWidth;
    private float dotRadius;
    private Paint paint;

    private int dotSize;

    public BackgroundComponent(Context context) {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
//        backgroundColor = ContextCompat.getColor(context, R.color.colorPrimary);
        backgroundColor = Color.parseColor("#7747F2");
//        dotColor = ContextCompat.getColor(context,R.color.orange);
        dotColor = Color.parseColor("#F9A01E");
        arcWidth = DisplayUtil.dip2px(context, 34f);
        dotRadius = DisplayUtil.dip2px(context, 8f);
        dotSize = 33;
    }

    @Override
    public void onDraw(LotteryView view, Canvas canvas, float rotateAngle) {
        int w = view.getWidth();
        int h = view.getHeight();
        int top = view.getLotteryTop();
        int bigCircleRadius = w / 2;
        int dotsCircleRadius = bigCircleRadius - arcWidth / 2;

        paint.setColor(backgroundColor);
        canvas.drawCircle(bigCircleRadius, bigCircleRadius + top, bigCircleRadius, paint);
        float dotAngleInDegree = CIRCLE_DEGREE / dotSize;
        paint.setColor(dotColor);
        float rotateAngleMod = Math.abs(rotateAngle % CIRCLE_DEGREE);
        Log.d(TAG, "angle:" + rotateAngleMod + "," + rotateAngle);
        for (int i = 0; i < dotSize; i++) {
            double angle = parseDegree2Arc(dotAngleInDegree * i + rotateAngleMod);
            double x = getCircleX(bigCircleRadius, angle, dotsCircleRadius);
            double y = getCircleY(bigCircleRadius + top, angle, dotsCircleRadius);
            canvas.drawCircle((float) x, (float) y, dotRadius, paint);
        }
        paint.setColor(Color.WHITE);
        canvas.drawCircle(bigCircleRadius, bigCircleRadius + top, bigCircleRadius - arcWidth, paint);
    }
}

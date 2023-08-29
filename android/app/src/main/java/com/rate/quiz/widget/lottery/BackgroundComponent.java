package com.rate.quiz.widget.lottery;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;

import com.duxl.baselib.utils.DisplayUtil;

public class BackgroundComponent extends Component {

    public static final String TAG = "BackgroundComponent";
    public static final float BORDER_WIDTH = 28f;

    private Shader bgShader;
    private int dotColor;
    private int arcWidth;
    private float dotRadius;
    private Paint paint;

    private int dotSize;

    public BackgroundComponent(Context context) {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        //#5A2782, #DA47F2
        dotColor = Color.WHITE;
        arcWidth = DisplayUtil.dip2px(context, BORDER_WIDTH);
        dotRadius = DisplayUtil.dip2px(context, 6f);
        dotSize = 33;
    }

    @Override
    public void onDraw(LotteryView view, Canvas canvas, float rotateAngle) {
        int w = view.getWidth();
        int h = view.getHeight();
        int top = view.getLotteryTop();
        int bigCircleRadius = w / 2;
        int dotsCircleRadius = bigCircleRadius - arcWidth / 2;

        if (bgShader == null) {
            bgShader = new LinearGradient(0f, 0f, 0f, h, new int[]{
                    Color.parseColor("#DA47F2"),
                    Color.parseColor("#5A2782")
            }, null, Shader.TileMode.CLAMP);
        }

        paint.setShader(bgShader);
        canvas.drawCircle(bigCircleRadius, bigCircleRadius + top, bigCircleRadius, paint);
        paint.setShader(null);
        float dotAngleInDegree = CIRCLE_DEGREE / dotSize;
        paint.setColor(dotColor);
        float rotateAngleMod = Math.abs(rotateAngle % CIRCLE_DEGREE);
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

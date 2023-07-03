package com.hash.coinconvert.widget.lottery;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.duxl.baselib.utils.DisplayUtil;

public class BackgroundComponent extends Component{

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
        arcWidth = DisplayUtil.dip2px(context,34f);
        dotRadius = DisplayUtil.dip2px(context,8f);
        dotSize = 32;
    }

    @Override
    public void onDraw(LotteryView view, Canvas canvas, float rotateAngle) {
        int w = view.getWidth();
        int h = view.getHeight();
        int top = view.getLotteryTop();
        int bigCircleRadius = w/2;
        int dotsCircleRadius = bigCircleRadius - arcWidth/2;

        paint.setColor(backgroundColor);
        canvas.drawCircle(bigCircleRadius,bigCircleRadius+top,bigCircleRadius,paint);

        double dotAngle = (Math.pow(dotsCircleRadius,2)*2 - Math.pow(dotRadius*2,2))/(2 * dotsCircleRadius * dotsCircleRadius);
        double dotArcLength = dotAngle * dotsCircleRadius;

        Log.d(TAG,"dotAngle:"+dotAngle+",dotArcLength:"+dotArcLength);

        double dotsCircleLength = Math.PI * 2 * bigCircleRadius;
        double dotSpace = (dotsCircleLength-dotArcLength*dotSize)/(dotSize-1);
        double dotSpaceAngle = dotSpace / dotsCircleRadius;
        Log.d(TAG,"dotSpaceAngle:"+dotSpaceAngle+",dotSpaceArcLength:"+dotsCircleLength);

        paint.setColor(dotColor);
        float rotateAngleInArc = parseDegree2Arc(rotateAngle);
        for (int i = 0; i < dotSize+1; i++) {
            double angle = (dotAngle+dotSpaceAngle) * i + rotateAngleInArc;
            double x = getCircleX(bigCircleRadius,angle,dotsCircleRadius);
            double y = getCircleY(bigCircleRadius+top,angle,dotsCircleRadius);
            canvas.drawCircle((float) x, (float) y,dotRadius,paint);
        }
        paint.setColor(Color.WHITE);
        canvas.drawCircle(bigCircleRadius,bigCircleRadius+top,bigCircleRadius-arcWidth,paint);
    }
}

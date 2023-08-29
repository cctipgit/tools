package com.rate.quiz.widget.lottery;

import android.graphics.Canvas;

public abstract class Component {

    public static final float CIRCLE_DEGREE = 360f;
    public static final float CIRCLE_ARC = (float) (Math.PI * 2);

    /**
     * 具体绘制方法
     * @param view 绘制的view
     * @param canvas view
     * @param rotateAngle 当前旋转角度degree
     */
    public abstract void onDraw(LotteryView view,Canvas canvas,float rotateAngle);

    /**
     * 根据角度和半径计算圆上某点的坐标x
     * @param cx 圆点x坐标
     * @param angle 弧度
     * @param radius 半径
     * @return x坐标
     */
    public double getCircleX(int cx,double angle,int radius){
        return cx + radius * Math.cos(angle);
    }

    /**
     * 根据角度和半径计算圆上某点的坐标x
     * @param cy 圆点y坐标
     * @param angle 弧度
     * @param radius 半径
     * @return y坐标
     */
    public double getCircleY(int cy,double angle,int radius){
        return cy + radius * Math.sin(angle);
    }


    public float parseDegree2Arc(float degree) {
        return (float) (degree * Math.PI * 2 / CIRCLE_DEGREE);
    }
    public float parseArc2Degree(float arc){
        return  arc*CIRCLE_DEGREE/CIRCLE_ARC;
    }
}

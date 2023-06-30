package com.hash.coinconvert.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ShaderEditText extends AppEditText {

    private LinearGradient mGradient;
    private boolean mShowShader;

    public ShaderEditText(@NonNull Context context) {
        super(context);
    }

    public ShaderEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTextGradient(boolean show) {
        this.mShowShader = show;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        /*if(mShowShader && getText().length() > 0) {
            if(mGradient == null) {
                mGradient = new LinearGradient(10, getMeasuredHeight() / 2, 200, getMeasuredHeight() / 2,
                        new int[]{Color.TRANSPARENT, getCurrentTextColor()},
                        new float[]{0.f, 0.5f},
                        Shader.TileMode.CLAMP);
            }
            getPaint().setShader(mGradient);
        } else {
            getPaint().setShader(null);
        }*/
        super.onDraw(canvas);

    }
}

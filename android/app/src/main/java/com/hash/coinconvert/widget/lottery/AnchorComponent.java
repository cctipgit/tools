package com.hash.coinconvert.widget.lottery;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.duxl.baselib.utils.DisplayUtil;
import com.hash.coinconvert.R;

public class AnchorComponent extends Component {
    private final Drawable mid;
    private final int midSize;

    private final Drawable top;
    private final int topWidth;
    private final int topHeight;

    public AnchorComponent(Context context) {
        mid = ContextCompat.getDrawable(context, R.mipmap.ic_wheel_center);
        midSize = DisplayUtil.dip2px(context, 47.65f);

        top = ContextCompat.getDrawable(context,R.drawable.ic_lottery_fixed_anchor);
        topWidth = DisplayUtil.dip2px(context,35.4f);
        topHeight = DisplayUtil.dip2px(context,61.2f);
    }

    @Override
    public void onDraw(LotteryView view, Canvas canvas, float rotateAngle) {
        int w = view.getWidth();
        int h = view.getHeight();
        int cx = w / 2;
        int cy = h - cx;

        int half = midSize / 2;
        mid.setBounds(cx - half, cy - half, cx + half, cy + half);
        mid.draw(canvas);

        top.setBounds(cx-topWidth/2,0,cx+topWidth/2,topHeight);
        top.draw(canvas);
    }
}

package com.rate.quiz.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.rate.quiz.R;
import com.rate.quiz.utils.FontUtil;

public class AppTextView extends AppCompatTextView {

    public AppTextView(@NonNull Context context) {
        this(context, null);
    }

    public AppTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
//        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AppTextView);
        if (typedArray != null) {
            int fontType = typedArray.getInt(R.styleable.AppTextView_fountType, 3);
            if(fontType != -1) {
                FontUtil.setType(this, FontUtil.FOUNT_TYPE.values()[fontType]);
            }
            typedArray.recycle();
        }
    }

    /**
     * 排除每行文字间的padding
     *
     * @param text
     */
    public void setCustomText(CharSequence text) {
        /*if (text == null) {
            return;
        }

        // 获得视觉定义的每行文字的行高
        int lineHeight = (int) getTextSize();

        SpannableStringBuilder ssb ;
        if (text instanceof SpannableStringBuilder) {
            ssb = (SpannableStringBuilder) text;
            // 设置LineHeightSpan
            ssb.setSpan(new ExcludeInnerLineSpaceSpan(lineHeight),
                    0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            ssb = new SpannableStringBuilder(text);
            // 设置LineHeightSpan
            ssb.setSpan(new ExcludeInnerLineSpaceSpan(lineHeight),
                    0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // 调用系统setText()方法
        setText(ssb);*/
        super.setText(text);
    }
}

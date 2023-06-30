package com.hash.coinconvert.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.duxl.baselib.utils.SPUtils;
import com.hash.coinconvert.Constants;
import com.hash.coinconvert.R;
import com.hash.coinconvert.databinding.LayoutNumberKeyboardViewBinding;
import com.hash.coinconvert.utils.Dispatch;
import com.hash.coinconvert.utils.FontUtil;
import com.hash.coinconvert.utils.SoundPoolManager;

public class NumberKeyboardView extends FrameLayout implements DefaultLifecycleObserver {

    private static final String TAG = "NumberKeyboardView";

    private LayoutNumberKeyboardViewBinding mBinding;
    private OnKeyInputListener mOnKeyInputListener;

    public NumberKeyboardView(@NonNull Context context) {
        this(context, null);
    }

    public NumberKeyboardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberKeyboardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NumberKeyboardView);
        if (typedArray != null) {
            typedArray.recycle();
        }

        View view = LayoutInflater.from(context).inflate(R.layout.layout_number_keyboard_view, this, false);
        mBinding = LayoutNumberKeyboardViewBinding.bind(view);
        addView(view);
        // 设置点击事件
        mBinding.tvNum0.setOnClickListener(v -> dispatchCode(v, 0));
        mBinding.tvNum1.setOnClickListener(v -> dispatchCode(v, 1));
        mBinding.tvNum2.setOnClickListener(v -> dispatchCode(v, 2));
        mBinding.tvNum3.setOnClickListener(v -> dispatchCode(v, 3));
        mBinding.tvNum4.setOnClickListener(v -> dispatchCode(v, 4));
        mBinding.tvNum5.setOnClickListener(v -> dispatchCode(v, 5));
        mBinding.tvNum6.setOnClickListener(v -> dispatchCode(v, 6));
        mBinding.tvNum7.setOnClickListener(v -> dispatchCode(v, 7));
        mBinding.tvNum8.setOnClickListener(v -> dispatchCode(v, 8));
        mBinding.tvNum9.setOnClickListener(v -> dispatchCode(v, 9));

        mBinding.tvFunPlus.setOnClickListener(v -> dispatchCode(v, -1));
        mBinding.tvFunMinus.setOnClickListener(v -> dispatchCode(v, -2));
        mBinding.tvFunTimes.setOnClickListener(v -> dispatchCode(v, -3));
        mBinding.tvFunDiv.setOnClickListener(v -> dispatchCode(v, -4));
        mBinding.tvNumDot.setOnClickListener(v -> dispatchCode(v, -5));
        mBinding.flFunDel.setOnClickListener(v -> dispatchCode(v, -99));
        mBinding.flFunDel.setOnLongClickListener(view1 -> {
            dispatchCode(view1, -88);
            return true;
        });
        mBinding.tvFunTimes.setOnLongClickListener(v -> {
            dispatchCode(v, -30);
            return true;
        });
        mBinding.tvFunDiv.setOnLongClickListener(v -> {
            dispatchCode(v, -40);
            return true;
        });

        for (View keyView : getAllKeyView()) {
            if(keyView instanceof TextView) {
                FontUtil.setType((TextView) keyView, FontUtil.FOUNT_TYPE.POPPINS_REGULAR);
            }
        }
    }

    private View[] getAllKeyView() {
        return new View[]{
                mBinding.tvNum0,
                mBinding.tvNum1,
                mBinding.tvNum2,
                mBinding.tvNum3,
                mBinding.tvNum4,
                mBinding.tvNum5,
                mBinding.tvNum6,
                mBinding.tvNum7,
                mBinding.tvNum8,
                mBinding.tvNum9,
                mBinding.tvFunPlus,
                mBinding.tvFunMinus,
                mBinding.tvFunTimes,
                mBinding.tvFunDiv,
                mBinding.tvNumDot,
                mBinding.flFunDel
        };
    }
//    @Override
//    public void onResume(@NonNull LifecycleOwner owner) {
//        boolean soundEffectsEnabled = SPUtils.getInstance().getBoolean(Constants.SP.KEY.KEYBOARD_SOUND, Constants.SP.DEFAULT.KEYBOARD_SOUND);
//        for (View view : getAllKeyView()) {
//            view.setSoundEffectsEnabled(soundEffectsEnabled);
//        }
//    }
    private void dispatchCode(View v, int code) {
        if (delayClickEnable) { // 快速点击处理，100毫秒内只响应一次点击事件
            boolean soundEffectsEnabled = SPUtils.getInstance().getBoolean(Constants.SP.KEY.KEYBOARD_SOUND, Constants.SP.DEFAULT.KEYBOARD_SOUND);
            if(soundEffectsEnabled) {
//                v.setSoundEffectsEnabled(true);
                SoundPoolManager.performStandardAudioFeedback(code);
            }

            Log.i(TAG, "数字键盘输入：" + code);
            if (mOnKeyInputListener != null) {
                mOnKeyInputListener.onKeyInput(code);
            }
            delayClickEnable = false;
            Dispatch.I.postUIDelayed(delayClickRunnable, 100);
        }
    }

    private boolean delayClickEnable = true;
    private Runnable delayClickRunnable = new Runnable() {
        @Override
        public void run() {
            delayClickEnable = true;
        }
    };

    public void setLifecycleOwner(LifecycleOwner owner) {
        if (owner == null) {
            return;
        }
        owner.getLifecycle().addObserver(this);
    }

    public interface OnKeyInputListener {
        /**
         * 按键输入
         *
         * @param code 0～9表示对应的数字按键，-99删除,-88长按删除, -30长按乘, -40长按除，-1加，-2减，-3乘，-4除，-5小数点
         */
        void onKeyInput(int code);
    }

    public void setOnKeyInputListener(OnKeyInputListener listener) {
        this.mOnKeyInputListener = listener;
    }
}

package com.hash.coinconvert.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.duxl.baselib.utils.DisplayUtil;
import com.hash.coinconvert.R;

public class ActionBar extends ConstraintLayout {

    public static final int CENTER = 0;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;

    private ImageButton backView;
    private TextView titleView;
    private LinearLayout menusLayout;
    private int singleMenuViewId = 0;

    @IntDef({CENTER, LEFT, RIGHT})
    public @interface Gravity {

    }

    @Gravity
    private int titleGravity = CENTER;

    public ActionBar(@NonNull Context context) {
        this(context, null);
    }

    public ActionBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActionBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ActionBar);
        initTitle(ta);
        initBackView(ta);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && ta.getBoolean(R.styleable.ActionBar_abAutoPaddingTop, true)) {
            setPadding(getPaddingLeft(), getPaddingTop() + getStatusBarHeight(), getPaddingRight(), getPaddingBottom());
        }
        ta.recycle();
    }

    private void initTitle(TypedArray ta) {
        String title = ta.getString(R.styleable.ActionBar_abTitle);
        titleGravity = ta.getInt(R.styleable.ActionBar_abTitleGravity, CENTER);
        if (!TextUtils.isEmpty(title)) {
            ensureTitleView();
            titleView.setText(title);
        }
    }

    private void initBackView(TypedArray ta) {
        boolean visible = ta.getBoolean(R.styleable.ActionBar_abBackIconVisible, true);
        if (!visible) {
            return;
        }
        ensureBackView();
        Drawable drawable = ta.getDrawable(R.styleable.ActionBar_abBackIcon);
        if (drawable == null) {
            backView.setImageResource(R.drawable.ic_actionbar_arrow_left);
        } else {
            backView.setImageDrawable(drawable);
        }
    }

    private void ensureTitleView() {
        if (titleView == null) {
            titleView = new TextView(getContext(), null, 0, R.style.TextView_Bold);
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            int paddingH = DisplayUtil.dip2px(getContext(), 10f);
            titleView.setPadding(paddingH, 0, paddingH, 0);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.topToTop = LayoutParams.PARENT_ID;
            params.bottomToBottom = LayoutParams.PARENT_ID;
            switch (titleGravity) {
                case LEFT:
                    params.leftToLeft = LayoutParams.PARENT_ID;
                    params.leftMargin = DisplayUtil.dip2px(getContext(), 56f);
                    break;
                case RIGHT:
                    params.rightToRight = LayoutParams.PARENT_ID;
                    params.rightToRight = DisplayUtil.dip2px(getContext(), 56f);
                    break;
                case CENTER:
                    params.leftToLeft = LayoutParams.PARENT_ID;
                    params.rightToRight = LayoutParams.PARENT_ID;
                    break;
            }

            titleView.setLayoutParams(params);

            addView(titleView);
        }
    }

    private void ensureBackView() {
        if (backView == null) {
            backView = new ImageButton(getContext(), null, 0, R.style.ImageButton_DayNight);
            backView.setBackground(null);

            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.topToTop = LayoutParams.PARENT_ID;
            params.leftToLeft = LayoutParams.PARENT_ID;
            params.bottomToBottom = LayoutParams.PARENT_ID;
            params.setMarginStart(DisplayUtil.dip2px(getContext(), 20f));

            backView.setLayoutParams(params);
            addView(backView);
            backView.setOnClickListener(v -> {
                Context context = getContext();
                if (context instanceof Activity) {
                    ((Activity) context).onBackPressed();
                }
            });
        }
    }

    public void addMenu(@DrawableRes int drawableResId, OnClickListener listener) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), drawableResId);
        if (drawable != null) {
            addMenu(drawable, listener);
        }
    }

    public void addMenu(Drawable drawable, OnClickListener listener) {
        ImageButton button = genMenuView(singleMenuViewId == 0);
        button.setImageDrawable(drawable);
        button.setOnClickListener(listener);
    }

    public void addMenu(View view) {
        if (singleMenuViewId == 0) {
            view.setLayoutParams(genMenuLayoutParams());
            singleMenuViewId = View.generateViewId();
            view.setId(singleMenuViewId);
            this.addView(view);
        } else {
            if (hasMenusLayout()) {
                menusLayout.addView(view);
            } else {
                ensureMenusLayout();
                View v = findViewById(singleMenuViewId);
                if (v != null) {
                    removeView(v);
                    menusLayout.addView(v);
                }
                menusLayout.addView(view);
            }
        }
    }

    public void addMenu(View view, OnClickListener listener) {
        addMenu(view);
        view.setOnClickListener(listener);
    }

    private ImageButton genMenuView(boolean single) {
        ImageButton imageButton = new ImageButton(getContext(), null, 0, R.style.ImageButton_DayNight);
        if (single) {
            imageButton.setLayoutParams(genMenuLayoutParams());
            singleMenuViewId = View.generateViewId();
            imageButton.setId(singleMenuViewId);
            this.addView(imageButton);
        } else {
            if (hasMenusLayout()) {
                menusLayout.addView(imageButton);
            } else {
                ensureMenusLayout();

                View v = findViewById(singleMenuViewId);
                if (v != null) {
                    removeView(v);
                    menusLayout.addView(v);
                }
                menusLayout.addView(imageButton);
            }
        }
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewGroup.LayoutParams layoutParams = imageButton.getLayoutParams();
                layoutParams.width = getMeasuredHeight();
                layoutParams.height = getMeasuredHeight();
                imageButton.setLayoutParams(layoutParams);
                ActionBar.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        return imageButton;
    }

    private LayoutParams genMenuLayoutParams() {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topToTop = LayoutParams.PARENT_ID;
        params.bottomToBottom = LayoutParams.PARENT_ID;
        params.rightToRight = LayoutParams.PARENT_ID;
        return params;
    }

    private void ensureMenusLayout() {
        menusLayout = new LinearLayout(getContext());
        menusLayout.setLayoutParams(genMenuLayoutParams());
        addView(menusLayout);
    }

    private boolean hasMenusLayout() {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof LinearLayout) {
                return true;
            }
        }
        return false;
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public TextView getTitleView() {
        return titleView;
    }

    public void setOnBackButtonClickListener(OnClickListener listener) {
        if (backView != null) {
            backView.setOnClickListener(listener);
        }
    }
}

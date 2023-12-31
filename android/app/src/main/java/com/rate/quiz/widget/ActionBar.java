package com.rate.quiz.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.duxl.baselib.utils.DisplayUtil;
import com.rate.quiz.R;

public class ActionBar extends ConstraintLayout {

    public static final int CENTER = 0;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int ICON_SIZE = 24;

    private ImageButton backView;
    private TextView titleView;
    private LinearLayout menusLayout;
    private boolean backViewVisible;

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
        backViewVisible = ta.getBoolean(R.styleable.ActionBar_abBackIconVisible, true);
        ensureItems();
        initBackView(ta);
        initTitle(ta);
        ta.recycle();
        setFitsSystemWindows(true);
    }

    private void initTitle(TypedArray ta) {
        String title = ta.getString(R.styleable.ActionBar_abTitle);
        titleGravity = ta.getInt(R.styleable.ActionBar_abTitleGravity, CENTER);
        updateTitleViewLayoutParams();
        if (!TextUtils.isEmpty(title)) {
            titleView.setText(title);
            int paddingV = DisplayUtil.dip2px(getContext(), 16f);
            titleView.setPadding(0, paddingV, 0, paddingV);
            int titleColor = ta.getColor(R.styleable.ActionBar_abTitleColor, Color.TRANSPARENT);
            if (titleColor != Color.TRANSPARENT) {
                titleView.setTextColor(titleColor);
            }
        }
    }

    private void initBackView(TypedArray ta) {
        Drawable drawable = ta.getDrawable(R.styleable.ActionBar_abBackIcon);
        if (drawable == null) {
            backView.setImageResource(R.drawable.ic_actionbar_arrow_left);
        } else {
            backView.setImageDrawable(drawable);
        }
        backView.setVisibility(backViewVisible ? View.VISIBLE : View.GONE);
    }

    public void setBackViewVisible(boolean visible) {
        if (visible == this.backViewVisible) return;
        this.backViewVisible = visible;
        backView.setVisibility(backViewVisible ? View.VISIBLE : View.GONE);
    }

    private void updateTitleViewLayoutParams() {
        LayoutParams params = null;
        int margin = DisplayUtil.dip2px(getContext(), 16f);
        switch (titleGravity) {
            case LEFT:
                params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftToRight = backView.getId();
                params.rightToLeft = menusLayout.getId();
                params.leftMargin = margin;
                params.rightMargin = margin;
                titleView.setGravity(android.view.Gravity.START);
                break;
            case RIGHT:
                params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.rightToLeft = menusLayout.getId();
                params.leftToRight = backView.getId();
                params.rightMargin = margin;
                params.leftMargin = margin;
                titleView.setGravity(android.view.Gravity.END);
                break;
            case CENTER:
            default:
                params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftToLeft = LayoutParams.PARENT_ID;
                params.rightToRight = LayoutParams.PARENT_ID;
                titleView.setMaxWidth((int) (DisplayUtil.getScreenWidth(getContext()) * 0.75f));
                titleView.setGravity(android.view.Gravity.CENTER);
                break;
        }
        params.topToTop = LayoutParams.PARENT_ID;
        params.bottomToBottom = LayoutParams.PARENT_ID;
        titleView.setLayoutParams(params);
    }

    private void ensureTitleView() {
        if (titleView == null) {
            titleView = new TextView(getContext(), null, android.R.attr.textViewStyle, R.style.TextView_Bold);
            titleView.setTextAppearance(R.style.TextView_Bold);
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            titleView.setMaxLines(1);
            titleView.setSingleLine(true);
            titleView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            updateTitleViewLayoutParams();
            addView(titleView);
        }
    }

    private void ensureBackView() {
        if (backView == null) {
            Context context = getContext();
            backView = new ImageButton(context, null, 0, R.style.ImageButton_DayNight);
            backView.setBackground(null);
            backView.setId(View.generateViewId());
            int marginV = DisplayUtil.dip2px(getContext(), 16f);
            int size = DisplayUtil.dip2px(context, 24f);
            LayoutParams params = new LayoutParams(size, size);
            params.topToTop = LayoutParams.PARENT_ID;
            params.leftToLeft = LayoutParams.PARENT_ID;
            params.bottomToBottom = LayoutParams.PARENT_ID;
            params.leftMargin = DisplayUtil.dip2px(context, 20f);
            params.topMargin = marginV;
            params.bottomMargin = marginV;

            backView.setLayoutParams(params);
            addView(backView);
            backView.setOnClickListener(v -> {
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
        if (drawable != null) {
            int size = DisplayUtil.dip2px(getContext(), ICON_SIZE);
            drawable.setBounds(0, 0, size, size);
        }
        ImageButton button = genMenuView();
        button.setImageDrawable(drawable);
        button.setOnClickListener(listener);
    }

    public void addMenu(View view) {
        if (view.getParent() != menusLayout) {
            menusLayout.addView(view);
        }
    }

    public void addMenu(View view, OnClickListener listener) {
        addMenu(view);
        view.setOnClickListener(listener);
    }

    private ImageButton genMenuView() {
        ImageButton imageButton = new ImageButton(getContext(), null, 0, R.style.ImageButton_DayNight);
        menusLayout.addView(imageButton);
        return imageButton;
    }

    private LayoutParams genMenuLayoutParams() {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topToTop = LayoutParams.PARENT_ID;
        params.bottomToBottom = LayoutParams.PARENT_ID;
        params.rightToRight = LayoutParams.PARENT_ID;
        params.rightMargin = DisplayUtil.dip2px(getContext(), 20f);
        return params;
    }

    private void ensureMenusLayout() {
        menusLayout = new LinearLayout(getContext());
        menusLayout.setLayoutParams(genMenuLayoutParams());
        menusLayout.setId(View.generateViewId());
        menusLayout.setPadding(DisplayUtil.dip2px(getContext(), 16f), 0, 0, 0);
        addView(menusLayout);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Nullable
    public TextView getTitleView() {
        return titleView;
    }

    public void setTitle(String title) {
        ensureTitleView();
        titleView.setText(title);
    }

    public void setOnBackButtonClickListener(OnClickListener listener) {
        if (backView != null) {
            backView.setOnClickListener(listener);
        }
    }

    public void setBackViewTintColor(int color) {
        if (backView != null) {
            Drawable drawable = backView.getDrawable();
            if (drawable != null) {
                drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            }
        }
    }

    public void ensureItems() {
        ensureBackView();
        ensureMenusLayout();
        ensureTitleView();
    }
}

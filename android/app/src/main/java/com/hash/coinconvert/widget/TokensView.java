package com.hash.coinconvert.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.hash.coinconvert.entity.TokenWrapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class TokensView extends LinearLayout {
    public static final String TAG = "TokensView";
    private List<TokenWrapper> tokens;

    private Map<String, TokenItemView> viewMap = new HashMap<>();

    public TokensView(Context context) {
        super(context);
        init();
    }

    public TokensView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TokensView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
    }

    public void setTokens(List<TokenWrapper> tokens) {
        if (this.tokens == null) this.tokens = new ArrayList<>();
        if (this.tokens.size() > 0) this.tokens.clear();
        tokens.sort(Comparator.comparing(new Function<TokenWrapper, String>() {
            @Override
            public String apply(TokenWrapper tokenWrapper) {
                return tokenWrapper.getSymbol();
            }
        }, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int min = Math.min(o1.length(), o2.length());
                for (int i = 0; i < min; i++) {
                    char c1 = o1.charAt(i);
                    char c2 = o2.charAt(i);
                    if (c1 != c2) {
                        return c1 - c2;
                    }
                }
                return 0;
            }
        }));
        this.tokens.addAll(tokens);
        initOrUpdate();
    }

    public void onAnchorChange(TokenWrapper token) {
        for (int i = 0; i < getChildCount(); i++) {
            TokenItemView view = (TokenItemView) getChildAt(i);
            view.setAnchorToken(token);
        }
    }

    public void clearAnchor() {
        for (int i = 0; i < getChildCount(); i++) {
            TokenItemView view = (TokenItemView) getChildAt(i);
            view.setAnchorToken(null);
        }
    }

    private void initOrUpdate() {
        Set<String> set = new HashSet<>();
        for (int i = 0; i < tokens.size(); i++) {
            TokenWrapper wrapper = tokens.get(i);
            String key = wrapper.getSymbol();
            set.add(key);
            TokenItemView itemView = viewMap.get(key);
            if (itemView == null) {
                itemView = new TokenItemView(getContext());
                viewMap.put(key, itemView);
                addView(itemView, i);
                itemView.setToken(wrapper);
            } else {
                //if this item is not added,add it
                if (itemView.getParent() == null) {
                    addView(itemView, i);
                }
            }
            itemView.setPrice(wrapper.getPrice().floatValue());
        }
        //remove non-exist items
        viewMap.entrySet().forEach(entry -> {
            if (!set.contains(entry.getKey())) {
                TokenItemView view = entry.getValue();
                if (view.getParent() == this) {
                    removeView(view);
                }
            }
        });
    }

    protected void onInputChange(TokenWrapper token) {
        Log.d(TAG, "onInputChange:" + token.input.toPlainString());
        for (int i = 0; i < getChildCount(); i++) {
            TokenItemView view = (TokenItemView) getChildAt(i);
            view.setAnchorToken(token);
        }
    }

    public static abstract class Adapter {
        public abstract int getItemCount();

        public abstract TokenItemView getView(int position);
    }
}

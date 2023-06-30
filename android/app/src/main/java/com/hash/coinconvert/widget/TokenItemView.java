package com.hash.coinconvert.widget;

import android.content.Context;
import android.hardware.input.InputManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.duxl.baselib.utils.SPUtils;
import com.hash.coinconvert.Constants;
import com.hash.coinconvert.R;
import com.hash.coinconvert.databinding.ItemHomeCurrencyListBinding;
import com.hash.coinconvert.entity.TokenWrapper;
import com.hash.coinconvert.utils.FlagLoader;

import java.math.BigDecimal;

public class TokenItemView extends LinearLayout{
    public static final String TAG = "TokenItemView";
    private ItemHomeCurrencyListBinding binding;

    private TokenWrapper anchorToken;
    private TokenWrapper token;

    public TokenItemView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public TokenItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TokenItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        View.inflate(context, R.layout.view_token_item, this);
        binding = ItemHomeCurrencyListBinding.bind(this);
//        binding.tvPrice.setOnClickListener(v -> {
//            binding.editText.requestFocus();
//        });
        binding.editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    onFocus();
                }
            }
        });
        binding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    BigDecimal b;
                    if (s.length() == 0) {
                        b = new BigDecimal(binding.editText.getHint().toString());
                    } else {
                        b = new BigDecimal(s.toString());
                    }
                    token.input = b;
                    ViewParent parent = getParent();
                    if (parent instanceof TokensView) {
                        ((TokensView) parent).onInputChange(token);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //ignore
                }
            }
        });
    }

    public void onFocus(){
        ViewParent parent = getParent();
        if (parent instanceof TokensView) {
            TokensView container = (TokensView) parent;
            container.onAnchorChange(token);
        }
//        showKeyboard();
    }

    private void showKeyboard() {
        Log.d(TAG, "showKeyboard");
        InputMethodManager manager = getContext().getSystemService(InputMethodManager.class);
        manager.showSoftInput(binding.editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public TokenWrapper getToken() {
        return token;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        binding = null;
        this.token = null;
        this.anchorToken = null;
    }

    public void setToken(TokenWrapper token) {
        this.token = token;
        FlagLoader.load(binding.imgLogo, 0, token.getLogo());
        binding.tvSymbol.setText(token.getSymbol());
        binding.tvName.setText(token.getName());
        setPrice(token.getPrice().floatValue());
    }

    public void setAnchorToken(TokenWrapper anchorToken) {
        Log.d(TAG, "setAnchorToken:" + (this.token == anchorToken));
        this.anchorToken = anchorToken;
        if (this.anchorToken == this.token) {
            //show EditText and hide TextView
            updateEditTextAlpha(1);
            int defInput = SPUtils.getInstance().getInt(Constants.SP.KEY.DEFAULT_CURRENCY_VALUE);
            binding.editText.setHint(String.valueOf(defInput));
        } else {
            updateEditTextAlpha(0);
            int decimals = getDecimals();
            binding.tvPrice.setText(this.token.priceOfAnchor(anchorToken, decimals).toPlainString());
        }
    }

    private void updateEditTextAlpha(int a) {
        binding.editText.setAlpha(a);
        binding.tvPrice.setAlpha(1 - a);
    }

    public void setPrice(float price) {
        Log.d(TAG,"setPrice:"+price);
        binding.llContent.setPrice(price);
        binding.tvPrice.setText(token.priceOfAnchor(anchorToken, getDecimals()).toPlainString());
    }

    private int getDecimals() {
        if (token.isCurrency()) {
            return SPUtils.getInstance().getInt(Constants.SP.KEY.DECIMICAL_LEGAL_TENDER, Constants.SP.DEFAULT.DECIMICAL_LEGAL_TENDER);
        } else {
            return SPUtils.getInstance().getInt(Constants.SP.KEY.DECIMICAL_CRYPTOCURRENCY, Constants.SP.DEFAULT.DECIMICAL_CRYPTOCURRENCY);
        }
    }
}

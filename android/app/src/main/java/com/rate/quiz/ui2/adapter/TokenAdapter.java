package com.rate.quiz.ui2.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.duxl.baselib.utils.SPUtils;
import com.rate.quiz.Constants;
import com.rate.quiz.R;
import com.rate.quiz.entity.TokenWrapper;
import com.rate.quiz.utils.FlagLoader;
import com.rate.quiz.widget.ColoredLayout;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class TokenAdapter extends BaseQuickAdapter<TokenWrapper, BaseViewHolder> implements TextWatcher, View.OnKeyListener {

    public static final String TAG = "TokenAdapter";
    public static final int PAYLOAD_UPDATE_PRICE = 0b0001;
    public static final int PAYLOAD_FOCUS_CHANGE = 0b0010;

    private final int decimals;

    private TokenWrapper anchorItem;
    private int defaultHint;
    private View focusedView;

    public TokenAdapter() {
        super(R.layout.item_home_currency_list);
        this.defaultHint = SPUtils.getInstance().getInt(Constants.SP.KEY.DEFAULT_CURRENCY_VALUE, Constants.SP.DEFAULT.DEFAULT_CURRENCY_VALUE);
        this.decimals = SPUtils.getInstance().getInt(Constants.SP.KEY.DECIMICAL_CRYPTOCURRENCY, Constants.SP.DEFAULT.DECIMICAL_CRYPTOCURRENCY);
        addChildClickViewIds(R.id.cl_detail);
    }

    private String getFormattedNumber(BigDecimal b) {
        if (b == null) return "0";
        return b.setScale(decimals, RoundingMode.DOWN).toString();
    }

    private void onFocusChange(@NonNull BaseViewHolder holder, TokenWrapper token) {
        TextView tvPrice = holder.getView(R.id.tv_price);
        tvPrice.setText(getFormattedNumber(token.getPrice()));
        EditText editText = holder.getView(R.id.edit_text);
        Log.d("OnFocusChange", token.getSymbol() + ":" + token.hasFocus);
        if (token.hasFocus) {
            editText.setAlpha(1f);
            tvPrice.setAlpha(0f);
            editText.setOnKeyListener(this);
            this.afterTextChanged(editText.getText());
        } else {
//            editText.setOnKeyListener(null);
            editText.setAlpha(0f);
            tvPrice.setAlpha(1f);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        Log.d("OnKeyDown", "code:" + keyCode);
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                v.clearFocus();
//                clearAnchor();
            }
            return true;
        }
        return false;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, TokenWrapper token) {
        FlagLoader.load(holder.getView(R.id.img_logo), 0, token.getLogo());
        holder.setText(R.id.tv_symbol, token.getSymbol());
        holder.setText(R.id.tv_name, token.getName());
        onFocusChange(holder, token);

        TextView tvPrice = holder.getView(R.id.tv_price);
        tvPrice.setText(getFormattedNumber(token.getPrice()));
        EditText editText = holder.getView(R.id.edit_text);
        editText.setHint(String.valueOf(defaultHint));

        if (editText.getOnFocusChangeListener() == null) {
            editText.setOnFocusChangeListener((v, hasFocus) -> {
                token.hasFocus = hasFocus;
                int pos = getItemPosition(anchorItem);
                if (hasFocus) {
                    focusedView = v;
                    if (anchorItem != null && anchorItem != token) {
                        anchorItem.hasFocus = false;
                    }
                    anchorItem = token;
                    notifyItemChanged(pos, PAYLOAD_FOCUS_CHANGE);
                }
                for (int i = 0; i < getItemCount(); i++) {
                    if (pos == i) continue;
                    try {
                        notifyItemChanged(i, PAYLOAD_FOCUS_CHANGE);
                    } catch (Exception e) {
                        //ignore
                    }
                }
            });
        }
        editText.removeTextChangedListener(this);
        editText.addTextChangedListener(this);
        if (getItemPosition(token) == 0) {
            holder.getView(R.id.cl_detail).setOnClickListener(v -> {
                OnItemChildClickListener listener = getOnItemChildClickListener();
                if (listener != null) {
                    listener.onItemChildClick(this, v, 0);
                }
            });
        }
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, TokenWrapper item, @NonNull List<?> payloads) {
        if (payloads.size() > 0) {
            try {
                int payload = (int) payloads.get(0);
                if ((payload & PAYLOAD_UPDATE_PRICE) == PAYLOAD_UPDATE_PRICE) {
                    TextView tvPrice = holder.getView(R.id.tv_price);
                    BigDecimal price = item.priceOfAnchor(anchorItem).setScale(decimals, RoundingMode.DOWN);
                    ((ColoredLayout) holder.getView(R.id.ll_content)).setPrice(price.floatValue());
                    String text = getFormattedNumber(price);
                    tvPrice.setText(text);
                }
                if ((payload & PAYLOAD_FOCUS_CHANGE) == PAYLOAD_FOCUS_CHANGE) {
                    onFocusChange(holder, item);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            super.convert(holder, item, payloads);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (anchorItem != null) {
            try {
                if (s.length() == 0) {
                    anchorItem.input = BigDecimal.valueOf(defaultHint);
                } else {
                    anchorItem.input = new BigDecimal(s.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
                anchorItem.input = BigDecimal.valueOf(defaultHint);
            }
            notifyItemRangeChanged(0, getItemCount(), PAYLOAD_UPDATE_PRICE);
        }
    }

    public void onKeyBoardVisibilityChange(boolean visible) {
        if (!visible) {
            clearAnchor();
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        clearAnchor();
    }

    private void clearAnchor() {
        if (anchorItem != null) {
            anchorItem.hasFocus = false;
        }
        if (focusedView != null) {
            focusedView.clearFocus();
            focusedView = null;
        }
        notifyItemRangeChanged(0, getItemCount(), PAYLOAD_FOCUS_CHANGE | PAYLOAD_UPDATE_PRICE);
    }

    public void destroyView() {
        anchorItem = null;
        focusedView = null;
    }
}

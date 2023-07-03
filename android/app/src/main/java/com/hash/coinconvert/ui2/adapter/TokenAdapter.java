package com.hash.coinconvert.ui2.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.duxl.baselib.utils.SPUtils;
import com.hash.coinconvert.Constants;
import com.hash.coinconvert.R;
import com.hash.coinconvert.entity.TokenWrapper;
import com.hash.coinconvert.utils.FlagLoader;
import com.hash.coinconvert.widget.ColoredLayout;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class TokenAdapter extends BaseQuickAdapter<TokenWrapper, BaseViewHolder> implements TextWatcher {

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
    }

    private String getFormattedNumber(BigDecimal b) {
        if (b == null) return "0";
        return b.setScale(decimals, RoundingMode.DOWN).toString();
    }

    private void onFocusChange(@NonNull BaseViewHolder holder, TokenWrapper token) {
        TextView tvPrice = holder.getView(R.id.tv_price);
        tvPrice.setText(getFormattedNumber(token.getPrice()));
        EditText editText = holder.getView(R.id.edit_text);

        if (token.hasFocus) {
            editText.setAlpha(1f);
            tvPrice.setAlpha(0f);
            this.afterTextChanged(editText.getText());
        } else {
            editText.setAlpha(0f);
            tvPrice.setAlpha(1f);
        }
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, TokenWrapper token) {
        addChildClickViewIds(R.id.cl_detail);
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
                        notifyItemChanged(pos, PAYLOAD_FOCUS_CHANGE);
                    }
                    anchorItem = token;
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
                    anchorItem.input = BigDecimal.ONE;
                } else {
                    anchorItem.input = new BigDecimal(s.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
                anchorItem.input = BigDecimal.ONE;
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

    public void destroyView(){
        anchorItem = null;
        focusedView = null;
    }
}

package com.hash.coinconvert.ui2.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;

import com.hash.coinconvert.R;
import com.hash.coinconvert.databinding.DialogSelectTypeBinding;
import com.hash.coinconvert.ui.activity.SearchCurrencyActivity;
import com.hash.coinconvert.ui.activity.SearchCurrencyActivity.SelectType;

public class SelectTypeDialog extends BaseBottomSheetDialog {
    private DialogSelectTypeBinding binding;
    private static final String KEY = "type";
    @SelectType
    private int type;
    private OnSelectTypeChangeListener listener;

    public static SelectTypeDialog newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt(KEY, type);
        SelectTypeDialog fragment = new SelectTypeDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getTheme() {
        return R.style.Theme_ExchangeRate_Dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSelectTypeChangeListener) {
            listener = (OnSelectTypeChangeListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_select_type, container, false);
        binding = DialogSelectTypeBinding.bind(view);
        return view;
    }

    private void ensureType() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            type = bundle.getInt(KEY);
        } else {
            type = SearchCurrencyActivity.SELECT_TYPE_CURRENCY;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.actionBar.setOnBackButtonClickListener(v -> {
            dismiss();
        });
        for (int i = 0; i < binding.radios.getChildCount(); i++) {
            RadioButton rb = (RadioButton) binding.radios.getChildAt(i);
            rb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    int color = ContextCompat.getColor(requireContext(), R.color.theme_text_color);
                    buttonView.setTextColor(color);
                    TextViewCompat.setCompoundDrawableTintList(buttonView,ColorStateList.valueOf(color));
                    buttonView.getPaint().setFakeBoldText(true);
                } else {
                    int color = ContextCompat.getColor(requireContext(), R.color.sub_title);
                    buttonView.setTextColor(color);
                    TextViewCompat.setCompoundDrawableTintList(buttonView,ColorStateList.valueOf(color));
                    buttonView.getPaint().setFakeBoldText(false);
                }
            });
        }
        ensureType();
        binding.radios.check(type);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if (listener != null) {
            listener.onSelectTypeChange(binding.radios.getCheckedRadioButtonId());
        }
        super.onDismiss(dialog);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public interface OnSelectTypeChangeListener {
        void onSelectTypeChange(int type);
    }
}

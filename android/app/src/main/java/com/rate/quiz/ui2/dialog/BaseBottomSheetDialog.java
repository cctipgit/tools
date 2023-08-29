package com.rate.quiz.ui2.dialog;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.rate.quiz.R;

public abstract class BaseBottomSheetDialog extends BottomSheetDialogFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getTheme() {
        return R.style.Theme_ExchangeRate_BottomSheet;
    }
}

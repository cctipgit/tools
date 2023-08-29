package com.rate.quiz.ui2.dialog;

import android.view.View;

import com.rate.quiz.R;
import com.rate.quiz.databinding.DialogQaSubmittedBinding;

public class QASubmittedDialog extends BaseFragmentDialog<DialogQaSubmittedBinding> {
    @Override
    public int getLayoutId() {
        return R.layout.dialog_qa_submitted;
    }

    @Override
    public DialogQaSubmittedBinding bind(View view) {
        return DialogQaSubmittedBinding.bind(view);
    }

    @Override
    protected void initView() {
        binding.btnClose.setOnClickListener(v -> dismiss());
        binding.btnDone.setOnClickListener(v -> dismiss());
    }
}

package com.hash.coinconvert.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

public abstract class BaseBindingFragment<VB extends ViewBinding> extends BaseFragment {
    protected VB binding;

    public BaseBindingFragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = bindView(view);
        initView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public abstract void initView();

    public abstract VB bindView(@NonNull View view);
}

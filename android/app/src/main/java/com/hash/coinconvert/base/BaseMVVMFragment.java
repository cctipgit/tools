package com.hash.coinconvert.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.duxl.baselib.utils.ToastUtils;
import com.hash.coinconvert.BuildConfig;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class BaseMVVMFragment<VM extends BaseViewModel, VB extends ViewBinding> extends BaseFragment {

    protected VM viewModel;
    protected VB binding;

    public BaseMVVMFragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(getVMClass());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = bindView(view);
        initView();
        observer();
    }

    @NonNull
    protected abstract VB bindView(View view);

    protected abstract void initView();

    @CallSuper
    protected void observer() {
        observer(viewModel.getError(), this::onError);
        observer(viewModel.isLoading(), this::onLoading);
    }

    protected <T> void observer(LiveData<T> data, Observer<T> observer) {
        data.observe(getViewLifecycleOwner(), observer);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.isLoading().removeObservers(requireActivity());
        viewModel.getError().removeObservers(requireActivity());
        binding = null;
    }

    protected void onError(Throwable throwable) {
        if (BuildConfig.DEBUG) {
            ToastUtils.show(throwable.getMessage());
        }
        onLoading(false);
    }

    protected void onLoading(boolean loading) {

    }

    public abstract Class<? extends VM> getVMClass();

    protected Class<? extends VM> getVMClassByReflection() throws ClassNotFoundException {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            Type type = this.getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) type;
                Type[] types = pt.getActualTypeArguments();
                assert types.length == 2;
                String vmClassName = types[0].getTypeName();
                return (Class<? extends VM>) ClassLoader.getSystemClassLoader().loadClass(vmClassName);
            }
        }
        return null;
    }
}

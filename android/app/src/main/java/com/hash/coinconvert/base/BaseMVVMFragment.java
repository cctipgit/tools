package com.hash.coinconvert.base;

import android.os.Bundle;
import android.util.Log;
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
import com.hash.coinconvert.R;
import com.hash.coinconvert.error.ServerException;
import com.hash.coinconvert.vm.ProfileViewModel;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.SocketException;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import retrofit2.HttpException;

public abstract class BaseMVVMFragment<VM extends BaseViewModel, VB extends ViewBinding> extends BaseFragment {

    protected VM viewModel;
    protected VB binding;

    public BaseMVVMFragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
    }

    private void initViewModel(){
        Class<?> clz = getVMClass();
        if(Objects.equals(clz.getCanonicalName(), ProfileViewModel.class.getCanonicalName())){
            viewModel = (VM) profileViewModel;
        }else{
            viewModel = new ViewModelProvider(this).get(getVMClass());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        initViewModel();
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
        viewModel.isLoading().removeObservers(this);
        viewModel.getError().removeObservers(this);
        viewModel.onViewDestroy();
        binding = null;
    }

    protected void onError(Throwable throwable) {
        if(throwable == null)return;
        throwable.printStackTrace();
        if (handleNetworkError(throwable)) return;
        if (handleServerError(throwable)) return;
        if (BuildConfig.DEBUG) {
            ToastUtils.show(throwable.getMessage());
        }
        onLoading(false);
    }

    public boolean handleServerError(Throwable throwable) {
        if (throwable instanceof ServerException) {
            ServerException err = (ServerException) throwable;
            ToastUtils.show(getErrorStringResIdByCode(err.code));
            return true;
        }
        return false;
    }

    protected int getErrorStringResIdByCode(int code) {
        switch (code) {
            case 400003:
                return R.string.err_insufficient_point;
            case 0:
            case 10009:
            case 10005:
            case 10003:
                return R.string.err_server_error;
        }
        return R.string.err_unknown;
    }

    protected boolean handleNetworkError(Throwable throwable) {
        if (throwable instanceof SocketException
                || throwable instanceof TimeoutException
                || throwable instanceof HttpException) {
            ToastUtils.show(R.string.err_request_faield);
            return true;
        }
        return false;
    }

    protected void onLoading(boolean loading) {
        if(loading)showLoading();
        else hideLoading();
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

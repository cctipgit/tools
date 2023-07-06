package com.hash.coinconvert.base;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hash.coinconvert.entity.TResponse;
import com.hash.coinconvert.error.ServerException;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseViewModel extends ViewModel {
    protected MutableLiveData<Throwable> error = new MutableLiveData<>();

    protected MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    public LiveData<Throwable> getError() {
        return error;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    protected boolean isInLoading(){
        return Boolean.TRUE.equals(loading.getValue());
    }

    public void onCreate(){

    }

    protected boolean isNotLoading(){
        return Boolean.FALSE.equals(loading.getValue());
    }

    public void startLoading(){
        if(isNotLoading()){
            loading.postValue(true);
        }
    }


    protected <T> void startLoadingIfNeeded(LiveData<T> liveData){
        if(liveData.getValue() == null){
            startLoading();
        }
    }

    protected <T> void execute(Observable<T> observable, Consumer<T> action) {
        observable
                .subscribeOn(Schedulers.computation())
                .subscribe(new Observer<T>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        //TODO dispose it when this viewModel is going to be cleared
                    }

                    @Override
                    public void onNext(@NonNull T t) {
                        try {
                            action.accept(t);
                        } catch (Throwable e) {
                            error.postValue(e);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        error.postValue(e);
                    }

                    @Override
                    public void onComplete() {
                        if (Boolean.TRUE.equals(loading.getValue())) {
                            loading.postValue(false);
                        }
                    }
                });
    }

    protected <T> void execute(Call<TResponse<T>> call, Action<T> action) {
        call.enqueue(new Callback<TResponse<T>>() {
            @Override
            public void onResponse(Call<TResponse<T>> call, Response<TResponse<T>> response) {
                finishLoading();
                TResponse<T> body = response.body();
                if (body != null) {
                    if (body.isSuccess()) {
                        action.invoke(body.data);
                    } else {
                        error.postValue(new ServerException(body.code, body.msg));
                    }
                } else {
                    error.postValue(new ServerException(TResponse.EMPTY_BODY, "server error"));
                }
            }

            @Override
            public void onFailure(Call<TResponse<T>> call, Throwable t) {
                finishLoading();
                error.postValue(t);
            }
        });
    }

    protected void finishLoading() {
        if (Boolean.TRUE.equals(loading.getValue())) {
            loading.postValue(false);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public void onViewDestroy(){
        error.setValue(null);
        loading.setValue(false);
    }
}

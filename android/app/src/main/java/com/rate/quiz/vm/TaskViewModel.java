package com.rate.quiz.vm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rate.quiz.base.BaseViewModel;
import com.rate.quiz.entity.PinList;
import com.rate.quiz.entity.TaskCheckRequest;
import com.rate.quiz.entity.TaskList;
import com.rate.quiz.http.RetrofitHelper;
import com.rate.quiz.http.api.ToolApi;

public class TaskViewModel extends BaseViewModel {
    private ToolApi api;

    private MutableLiveData<TaskList> taskList = new MutableLiveData<>();
    private MutableLiveData<String> checkResult = new MutableLiveData<>();

    private MutableLiveData<PinList> pinList = new MutableLiveData<>();

    private MutableLiveData<Integer> pinNum = new MutableLiveData<>();

    public LiveData<TaskList> getTaskList() {
        return taskList;
    }

    public LiveData<String> getCheckResult() {
        return checkResult;
    }

    public LiveData<PinList> getPinList(){
        return pinList;
    }

    public LiveData<Integer> getPinNum(){
        return pinNum;
    }

    public TaskViewModel() {
        api = RetrofitHelper.create(ToolApi.class);
    }

    public void fetchTasks() {
        startLoadingIfNeeded(taskList);
        execute(api.taskList(), data -> {
            taskList.postValue(data);
        });
    }

    public void checkTask(String taskId, String param) {
        startLoading();
        execute(api.taskCheck(new TaskCheckRequest(param, taskId)), s -> {
            checkResult.postValue(taskId);
            pinNum.postValue(s.pinNum);
        });
    }

    public void fetchPinList(){
        startLoadingIfNeeded(pinList);
        execute(api.pinList(),res -> {
            pinList.postValue(res);
        });
    }

    @Override
    public void onCreate() {
        fetchTasks();
        fetchPinList();
    }
}

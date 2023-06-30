package com.hash.coinconvert.vm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hash.coinconvert.base.BaseViewModel;
import com.hash.coinconvert.entity.TaskCheckRequest;
import com.hash.coinconvert.entity.TaskList;
import com.hash.coinconvert.http.RetrofitHelper;
import com.hash.coinconvert.http.api.ToolApi;

public class TaskViewModel extends BaseViewModel {
    private ToolApi api;

    private MutableLiveData<TaskList> taskList = new MutableLiveData<>();
    private MutableLiveData<String> checkResult = new MutableLiveData<>();

    public LiveData<TaskList> getTaskList() {
        return taskList;
    }

    public LiveData<String> getCheckResult() {
        return checkResult;
    }

    public TaskViewModel() {
        api = RetrofitHelper.create(ToolApi.class);
    }

    public void fetchTasks() {
        execute(api.taskList(), data -> {
            taskList.postValue(data);
        });
    }

    public void checkTask(String taskId, String param) {
        execute(api.taskCheck(new TaskCheckRequest(param, taskId)), s -> {
            checkResult.postValue(taskId);
        });
    }
}

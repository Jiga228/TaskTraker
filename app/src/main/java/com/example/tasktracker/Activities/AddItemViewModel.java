package com.example.tasktracker.Activities;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.tasktracker.Answers.Task;
import com.example.tasktracker.Api.ApiFactory;
import com.example.tasktracker.Api.TaskRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddItemViewModel extends AndroidViewModel {
    private static final String TAG = "ADD_ITEM_VIEW_MODEL";
    private final MutableLiveData<Boolean> liveDataSave = new MutableLiveData<>();
    private final MutableLiveData<String> Failure = new MutableLiveData<>();

    public AddItemViewModel(@NonNull Application application) {
        super(application);
    }

    public void OfflineSaveData(Task task) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                TaskRepository.newInstance(getApplication()).taskDao().addTask(task);
                liveDataSave.postValue(true);
            }
        }).start();
    }

    public void OnlineSaveData(Task task, String token) {

        Call<Void> send = ApiFactory.getApiService().addTask(token, task);
        send.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                Log.d(TAG, "Task add successfully");
                liveDataSave.postValue(true);
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable throwable) {
                Log.w(TAG, throwable.toString());
                Failure.postValue(throwable.getMessage());
            }
        });
    }

    public LiveData<String> getFailure() {
        return Failure;
    }
    public LiveData<Boolean> getClose() {
        return liveDataSave;
    }
}

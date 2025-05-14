package com.example.tasktracker.Activities.ViewModels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.tasktracker.Activities.EditTask;
import com.example.tasktracker.Answers.Task;
import com.example.tasktracker.Api.ApiFactory;
import com.example.tasktracker.Api.TaskDao;
import com.example.tasktracker.Api.TaskRepository;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditTaskViewModel extends AndroidViewModel {
    private static final String TAG = "EDIT_ITEM_VIEW_MODEL";

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final MutableLiveData<Boolean> liveDataSave = new MutableLiveData<>();
    private final MutableLiveData<String> liveDataFail = new MutableLiveData<>();
    private final MutableLiveData<Task> liveDataLoad = new MutableLiveData<>();

    public EditTaskViewModel(@NonNull Application application) {
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

        Call<Void> send = ApiFactory.getApiService().editTask(token, task);
        send.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                Log.d(TAG, "Task update successfully");
                liveDataSave.postValue(true);
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable throwable) {
                Log.w(TAG, throwable.toString());
                liveDataFail.postValue(throwable.getMessage());
            }
        });
    }

    public void LoadTaskOffline(long taskID, EditTask owner) {
        TaskRepository.newInstance(getApplication()).taskDao().getTask(taskID).observe(owner, new Observer<Task>() {
            @Override
            public void onChanged(Task task) {
                liveDataLoad.postValue(task);
            }
        });
    }

    public void LoadTaskOnline(String token, long taskID) {
        Disposable disposable = ApiFactory.getApiService().getTask(token, taskID)
                .subscribeOn(Schedulers.io()).subscribe(new Consumer<Task>() {
                    @Override
                    public void accept(Task task) throws Throwable {
                        liveDataLoad.postValue(task);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        liveDataFail.postValue(throwable.getMessage());
                    }
                });
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    public LiveData<Task> isLoading() {
        return liveDataLoad;
    }
    public LiveData<String> getLiveDataFail() {
        return liveDataFail;
    }
    public LiveData<Boolean> getClose() {
        return liveDataSave;
    }
}

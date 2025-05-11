package com.example.tasktracker.Activities;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.tasktracker.Answers.Task;
import com.example.tasktracker.Api.ApiFactory;
import com.example.tasktracker.Api.TaskRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainMenuViewModel extends AndroidViewModel {
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final MutableLiveData<List<Task>> Load = new MutableLiveData<>();
    private final MutableLiveData<String> failLoad = new MutableLiveData<>();

    public MainMenuViewModel(@NonNull Application application) {
        super(application);
    }

    public void OnlineUpdateList(String token) {
        Single<ArrayList<Task>> single = ApiFactory.getApiService().getTaskList(token);
        Disposable disposable = single.subscribeOn(Schedulers.io()).subscribe(new Consumer<ArrayList<Task>>() {
            @Override
            public void accept(ArrayList<Task> tasks) throws Throwable {
                Load.postValue(tasks);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Throwable {
                failLoad.postValue(throwable.getMessage());
            }
        });
        compositeDisposable.add(disposable);
    }

    public LiveData<List<Task>> getOnLoad() {
        return Load;
    }

    public LiveData<String> onFail() {
        return failLoad;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}

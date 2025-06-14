package com.samsungschool.tasktracker.Activities.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.samsungschool.tasktracker.Answers.Task;
import com.samsungschool.tasktracker.Api.ApiFactory;

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

    public void OnlineUpdateList(String login, String password) {
        Single<ArrayList<Task>> single = ApiFactory.getApiService().getTaskList(login, password);
        Disposable disposable = single.subscribeOn(Schedulers.io()).subscribe(new Consumer<ArrayList<Task>>() {
            @Override
            public void accept(ArrayList<Task> tasks)  {
                Load.postValue(tasks);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
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

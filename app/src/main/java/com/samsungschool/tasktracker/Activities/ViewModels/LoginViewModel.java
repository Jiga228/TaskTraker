package com.samsungschool.tasktracker.Activities.ViewModels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.samsungschool.tasktracker.Answers.StatusAnswer;
import com.samsungschool.tasktracker.Api.ApiFactory;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoginViewModel extends AndroidViewModel {
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final MutableLiveData<StatusAnswer> SingInSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> SingInFail = new MutableLiveData<>();
    private final MutableLiveData<StatusAnswer> SingUpSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> SingUpFail = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public void SingIn(String login, String password) {
        Disposable disposable = ApiFactory.getApiService().singin(login, password)
                .subscribeOn(Schedulers.io()).subscribe(new Consumer<StatusAnswer>() {
                    @Override
                    public void accept(StatusAnswer statusAnswer) {
                        SingInSuccess.postValue(statusAnswer);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        SingInFail.postValue(throwable.getMessage());
                    }
                });
        compositeDisposable.add(disposable);
    }

    public void SingUp(String login, String password) {
        Disposable disposable = ApiFactory.getApiService().singup(login, password)
                .subscribeOn(Schedulers.io()).subscribe(new Consumer<StatusAnswer>() {
                    @Override
                    public void accept(StatusAnswer statusAnswer) {
                        SingUpSuccess.postValue(statusAnswer);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        SingUpFail.postValue(throwable.getMessage());
                    }
                });
        compositeDisposable.add(disposable);
    }

    public MutableLiveData<StatusAnswer> getSingInSuccess() {
        return SingInSuccess;
    }

    public MutableLiveData<String> getSingInFail() {
        return SingInFail;
    }

    public MutableLiveData<StatusAnswer> getSingUpSuccess() {
        return SingUpSuccess;
    }

    public MutableLiveData<String> getSingUpFail() {
        return SingUpFail;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d("LoginViewModel", "onCleared");
        compositeDisposable.dispose();
    }
}

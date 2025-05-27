package com.example.tasktracker.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tasktracker.Answers.StatusAnswer;
import com.example.tasktracker.Api.ApiFactory;
import com.example.tasktracker.PublicKeyNames;
import com.example.tasktracker.R;
import com.example.tasktracker.databinding.ActivityLoginBinding;
import com.google.android.material.snackbar.Snackbar;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LOGIN_ACTIVITY";


    private ActivityLoginBinding binding;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private String login;
    private String password;

    @SuppressLint("UnsafeIntentLaunch")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        login = getSharedPreferences(PublicKeyNames.TOKEN_LIST, MODE_PRIVATE).getString(PublicKeyNames.LOGIN_KEY, null);
        password = getSharedPreferences(PublicKeyNames.TOKEN_LIST, MODE_PRIVATE).getString(PublicKeyNames.PASSWORD_KEY, null);


        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        if(login != null && password != null)
            SingIn();

        binding.buttonSingIn.setOnClickListener(v->{
            login = binding.fieldLogin.getText().toString();
            password = binding.fieldPassword.getText().toString();
            if(login.isEmpty()) {
                Snackbar.make(binding.getRoot(), R.string.snackbar_EnterLogin, Snackbar.LENGTH_SHORT).show();
                return;
            }
            if(password.isEmpty()) {
                Snackbar.make(binding.getRoot(), R.string.snackbar_EnterPassword, Snackbar.LENGTH_SHORT).show();
                return;
            }

            SingIn();
        });

        binding.buttonSingUp.setOnClickListener(v->{
            login = binding.fieldLogin.getText().toString();
            password = binding.fieldPassword.getText().toString();
            if(login.isEmpty()) {
                Snackbar.make(binding.getRoot(), R.string.snackbar_EnterLogin, Snackbar.LENGTH_SHORT).show();
                return;
            }
            if(password.isEmpty()) {
                Snackbar.make(binding.getRoot(), R.string.snackbar_EnterPassword, Snackbar.LENGTH_SHORT).show();
                return;
            }

            SingUp();
        });

        binding.buttonOffline.setOnClickListener(v->{
            startActivity(MainMenu.getIntent(this, null,null));
            finish();
        });
    }

    private void SingIn() {
        Disposable disposable = ApiFactory.getApiService().singin(login, password)
                .subscribeOn(Schedulers.io()).subscribe(new Consumer<StatusAnswer>() {
                    @Override
                    public void accept(StatusAnswer loginAnswer) throws Throwable {
                        if(loginAnswer.getStatus().equals("big")) {
                            Snackbar.make(binding.getRoot(), R.string.snackbar_InvalidLogin, Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        else if (loginAnswer.getStatus().equals("error")) {
                            Snackbar.make(binding.getRoot(), R.string.snackbar_InvalidLogin, Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        @SuppressLint("UnsafeIntentLaunch") Intent intent = MainMenu.getIntent(LoginActivity.this, login, password);
                        startActivity(intent);
                        finish();

                        getSharedPreferences(PublicKeyNames.TOKEN_LIST, MODE_PRIVATE).edit()
                                .putString(PublicKeyNames.LOGIN_KEY, login)
                                .putString(PublicKeyNames.PASSWORD_KEY, password)
                                .apply();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Log.e(TAG, throwable.getMessage());
                        Snackbar.make(binding.getRoot(), R.string.snackbar_InternetError, Snackbar.LENGTH_LONG).show();
                    }
                });
        compositeDisposable.add(disposable);
    }
    private void SingUp() {
        Disposable disposable = ApiFactory.getApiService().singup(login, password)
                .subscribeOn(Schedulers.io()).subscribe(new Consumer<StatusAnswer>() {
                    @Override
                    public void accept(StatusAnswer loginAnswer) throws Throwable {
                        if(loginAnswer.getStatus().equals("big")) {
                            Snackbar.make(binding.getRoot(), R.string.snackbar_BigLoginPassword, Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        else if(loginAnswer.getStatus().equals("big")) {
                            Snackbar.make(binding.getRoot(), R.string.snackbar_SmallLoginPassword, Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        else if (loginAnswer.getStatus().equals("error")) {
                            Snackbar.make(binding.getRoot(), R.string.snackbar_AlredyCreated, Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        @SuppressLint("UnsafeIntentLaunch") Intent intent = MainMenu.getIntent(LoginActivity.this, login, password);
                        startActivity(intent);
                        finish();

                        getSharedPreferences(PublicKeyNames.TOKEN_LIST, MODE_PRIVATE).edit()
                                .putString(PublicKeyNames.LOGIN_KEY, login)
                                .putString(PublicKeyNames.PASSWORD_KEY, password)
                                .apply();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Log.e(TAG, throwable.getMessage());
                        Snackbar.make(binding.getRoot(), R.string.snackbar_InternetError, Snackbar.LENGTH_LONG).show();
                    }
                });
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
package com.example.tasktracker.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tasktracker.Answers.LoginAnswer;
import com.example.tasktracker.Api.ApiFactory;
import com.example.tasktracker.PublicKeyNames;
import com.example.tasktracker.R;
import com.example.tasktracker.databinding.ActivityLoginBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LOGIN_ACTIVITY";
    private ActivityLoginBinding binding;
    private SharedPreferences sharedPreferences;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static Intent getIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @SuppressLint("UnsafeIntentLaunch")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences(PublicKeyNames.TOKEN_LIST, MODE_PRIVATE);
        String token = sharedPreferences.getString(PublicKeyNames.TOKEN_KEY, "");
        if(!token.isEmpty())
        {
            Disposable disposable = ApiFactory.getApiService()
                    .checkToken(token)
                    .observeOn(Schedulers.io()).subscribe(new Consumer<LoginAnswer>() {
                        @Override
                        public void accept(LoginAnswer loginAnswer) throws Throwable {
                            if(loginAnswer.getStatus().equals("ok")) {
                                Intent intent = MainMenu.getIntent(LoginActivity.this, token);
                                startActivity(intent);
                                finish();
                            } else {
                                sharedPreferences.edit().putString(PublicKeyNames.TOKEN_KEY, "").apply();
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Throwable {
                            Snackbar.make(binding.getRoot(), R.string.snackbar_InternetError, Snackbar.LENGTH_LONG).show();
                            Log.e(TAG, Objects.requireNonNull(throwable.getMessage()));
                        }
                    });
            compositeDisposable.add(disposable);
        }

        binding.buttonSingIn.setOnClickListener(v->{ SingIn(); });

        binding.buttonSingUp.setOnClickListener(v->{ SingUp(); });

        binding.buttonOffline.setOnClickListener(v->{
            Intent intent = MainMenu.getIntent(this, "null");
            startActivity(intent);
            finish();
        });
    }

    private void SingIn()
    {
        String login = binding.fieldLogin.getText().toString();
        if(login.isEmpty())
        {
            Snackbar.make(binding.getRoot(), R.string.snackbar_EnterLogin, Snackbar.LENGTH_SHORT).show();
            return;
        }

        String pass = binding.fieldPassword.getText().toString();
        if(pass.isEmpty())
        {
            Snackbar.make(binding.getRoot(), R.string.snackbar_EnterPassword, Snackbar.LENGTH_SHORT).show();
            return;
        }

        Single<LoginAnswer> loginAnswer = ApiFactory.getApiService().singin(login, pass);
        Disposable disposable = loginAnswer
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LoginAnswer>() {
                    @Override
                    public void accept(LoginAnswer loginAnswer) throws Throwable {
                        if (loginAnswer.getStatus().equals("error")) {
                            Snackbar.make(binding.getRoot(), R.string.snackbar_InvalidLogin, Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        String token = loginAnswer.getToken();
                        sharedPreferences.edit().putString(PublicKeyNames.TOKEN_KEY, token).apply();
                        OpenMainMenu(token);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Log.w(TAG, throwable.toString());
                        Snackbar.make(binding.getRoot(), R.string.snackbar_InternetError, Snackbar.LENGTH_LONG).show();
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void SingUp()
    {
        String login = binding.fieldLogin.getText().toString();
        if(login.isEmpty())
        {
            Snackbar.make(binding.getRoot(), R.string.snackbar_EnterLogin, Snackbar.LENGTH_SHORT).show();
            return;
        }

        String pass = binding.fieldPassword.getText().toString();
        if(pass.isEmpty())
        {
            Snackbar.make(binding.getRoot(), R.string.snackbar_EnterPassword, Snackbar.LENGTH_SHORT).show();
            return;
        }

        Single<LoginAnswer> loginAnswer = ApiFactory.getApiService().singup(login, pass);
        Disposable disposable = loginAnswer
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LoginAnswer>() {
                    @Override
                    public void accept(LoginAnswer loginAnswer) throws Throwable {
                        if (loginAnswer.getStatus().equals("error")) {
                            Snackbar.make(binding.getRoot(), R.string.snackbar_AlredyCreated, Snackbar.LENGTH_LONG).show();
                            return;
                        }

                        String token = loginAnswer.getToken();
                        sharedPreferences.edit().putString(PublicKeyNames.TOKEN_KEY, token).apply();
                        OpenMainMenu(token);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Snackbar.make(binding.getRoot(), R.string.snackbar_InternetError, Snackbar.LENGTH_LONG).show();
                        Log.w(TAG, throwable.toString());
                    }
                });
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    private void OpenMainMenu(String token)
    {
        @SuppressLint("UnsafeIntentLaunch")
        Intent intent = MainMenu.getIntent(this, token);
        startActivity(intent);
        finish();
    }
}
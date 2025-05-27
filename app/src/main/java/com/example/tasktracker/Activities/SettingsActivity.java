package com.example.tasktracker.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tasktracker.Answers.StatusAnswer;
import com.example.tasktracker.Api.ApiFactory;
import com.example.tasktracker.PublicKeyNames;
import com.example.tasktracker.R;
import com.example.tasktracker.databinding.ActivitySettingsBinding;
import com.google.android.material.snackbar.Snackbar;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding;
    private CompositeDisposable compositeDisposable;

    private String login;

    public static Intent getIntent(Context context, String login) {
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.putExtra(PublicKeyNames.LOGIN_KEY, login);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        login = getIntent().getStringExtra(PublicKeyNames.LOGIN_KEY);

        binding.login.setText(login);

        binding.buttonBack.setOnClickListener(v->{
            finish();
        });
        binding.buttonLogOut.setOnClickListener(v->{
            getSharedPreferences(PublicKeyNames.TOKEN_LIST, MODE_PRIVATE)
                    .edit()
                    .remove(PublicKeyNames.LOGIN_KEY)
                    .remove(PublicKeyNames.PASSWORD_KEY)
                    .apply();
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        compositeDisposable = new CompositeDisposable();

        binding.buttonChangePassword.setOnClickListener(v->{
            String oldPassword = binding.fieldOldPassword.getText().toString();
            String newPassword = binding.fieldNewPassword.getText().toString();
            String retryPassword = binding.fieldRetreyPassword.getText().toString();

            if(!retryPassword.equals(newPassword)) {
                Snackbar.make(binding.getRoot(), R.string.snackbar_PasswordsNotEquals, Snackbar.LENGTH_LONG).show();
                return;
            }

            Disposable disposable = ApiFactory.getApiService().changePassword(oldPassword, login, newPassword)
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<StatusAnswer>() {
                        @Override
                        public void accept(StatusAnswer statusAnswer) throws Throwable {
                            if (statusAnswer.getStatus().equals("ok")) {
                                getSharedPreferences(PublicKeyNames.TOKEN_LIST, MODE_PRIVATE)
                                        .edit()
                                        .remove(PublicKeyNames.LOGIN_KEY)
                                        .remove(PublicKeyNames.PASSWORD_KEY)
                                        .apply();
                                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Snackbar.make(binding.getRoot(), R.string.snackbar_InvalidPassword, Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Throwable {
                            Snackbar.make(binding.getRoot(), R.string.snackbar_InternetError, Snackbar.LENGTH_SHORT).show();
                        }
                    });
            compositeDisposable.add(disposable);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
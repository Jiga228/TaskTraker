package com.samsungschool.tasktracker.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.samsungschool.tasktracker.Activities.ViewModels.LoginViewModel;
import com.samsungschool.tasktracker.Answers.StatusAnswer;
import com.samsungschool.tasktracker.PublicKeyNames;
import com.samsungschool.tasktracker.R;
import com.samsungschool.tasktracker.databinding.ActivityLoginBinding;
import com.google.android.material.snackbar.Snackbar;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LOGIN_ACTIVITY";

    private ActivityLoginBinding binding;

    private LoginViewModel viewModel;
    private String login;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        viewModel = new LoginViewModel(getApplication());
        login = getSharedPreferences(PublicKeyNames.TOKEN_LIST, MODE_PRIVATE).getString(PublicKeyNames.LOGIN_KEY, null);
        password = getSharedPreferences(PublicKeyNames.TOKEN_LIST, MODE_PRIVATE).getString(PublicKeyNames.PASSWORD_KEY, null);


        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        viewModel.getSingInSuccess().observe(this, new Observer<StatusAnswer>() {
            @Override
            public void onChanged(StatusAnswer statusAnswer) {
                Log.d(TAG, "good");
                if(statusAnswer.getStatus().equals("big")) {
                    Snackbar.make(binding.getRoot(), R.string.snackbar_InvalidLogin, Snackbar.LENGTH_SHORT).show();
                    return;
                }
                else if (statusAnswer.getStatus().equals("error")) {
                    Snackbar.make(binding.getRoot(), R.string.snackbar_InvalidLogin, Snackbar.LENGTH_SHORT).show();
                    return;
                }
                Log.d(TAG, "good1");

                getSharedPreferences(PublicKeyNames.TOKEN_LIST, MODE_PRIVATE).edit()
                        .putString(PublicKeyNames.LOGIN_KEY, login)
                        .putString(PublicKeyNames.PASSWORD_KEY, password)
                        .apply();

                startActivity(MainMenu.getIntent(LoginActivity.this, login, password));
                finish();
            }
        });
        viewModel.getSingInFail().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d(TAG, "fail");
                Log.e(TAG, s);
                Snackbar.make(binding.getRoot(), R.string.snackbar_InternetError, Snackbar.LENGTH_LONG).show();
            }
        });
        viewModel.getSingUpSuccess().observe(this, new Observer<StatusAnswer>()     {
            @Override
            public void onChanged(StatusAnswer statusAnswer) {
                switch (statusAnswer.getStatus()) {
                    case "big":
                        Snackbar.make(binding.getRoot(), R.string.snackbar_BigLoginPassword, Snackbar.LENGTH_SHORT).show();
                        return;
                    case "small":
                        Snackbar.make(binding.getRoot(), R.string.snackbar_SmallLoginPassword, Snackbar.LENGTH_SHORT).show();
                        return;
                    case "error":
                        Snackbar.make(binding.getRoot(), R.string.snackbar_AlredyCreated, Snackbar.LENGTH_SHORT).show();
                        return;
                }

                getSharedPreferences(PublicKeyNames.TOKEN_LIST, MODE_PRIVATE).edit()
                        .putString(PublicKeyNames.LOGIN_KEY, login)
                        .putString(PublicKeyNames.PASSWORD_KEY, password)
                        .apply();

                @SuppressLint("UnsafeIntentLaunch") Intent intent = MainMenu.getIntent(LoginActivity.this, login, password);
                startActivity(intent);
                finish();
            }
        });
        viewModel.getSingUpFail().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.e(TAG, s);
                Snackbar.make(binding.getRoot(), R.string.snackbar_InternetError, Snackbar.LENGTH_LONG).show();
            }
        });

        if(login != null && password != null)
            viewModel.SingIn(login, password);

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

            viewModel.SingIn(login, password);
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

            viewModel.SingUp(login, password);
        });

        binding.buttonOffline.setOnClickListener(v->{
            startActivity(MainMenu.getIntent(this, null,null));
            finish();
        });
    }
}
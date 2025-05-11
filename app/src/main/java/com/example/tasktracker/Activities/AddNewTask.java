package com.example.tasktracker.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.tasktracker.Answers.Task;
import com.example.tasktracker.R;
import com.example.tasktracker.databinding.ActivityAddNewTaskBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Random;

public class AddNewTask extends AppCompatActivity {
    private static final String TAG = "ADD_NEW_TASK";
    private static final String TOKEN_KEY = "TOKEN";

    private ActivityAddNewTaskBinding binding;
    private static String token;
    private AddItemViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityAddNewTaskBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        token = getIntent().getStringExtra(TOKEN_KEY);
        if(token == null)
        {
            Log.w(TAG, "Token is null");
            return;
        }

        viewModel = new ViewModelProvider(this).get(AddItemViewModel.class);
        viewModel.getClose().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean)
                    finish();
            }
        });

        // Offline
        if(token.equals("null")) {
            Offline();
        }
        // Online
        else {
            Online();
        }
    }

    private void Offline() {
        binding.AddTask.setOnClickListener(v->{
            String name = binding.TaskName.getText().toString();
            String description = binding.TaskDescription.getText().toString();
            String date = getDate();

            if(name.isEmpty()) {
                Snackbar.make(binding.getRoot(), R.string.snackbar_EnterName, Snackbar.LENGTH_SHORT).show();
                return;
            }
            if(date.isEmpty()) {
                Snackbar.make(binding.getRoot(), R.string.snackbar_EnterDate, Snackbar.LENGTH_SHORT).show();
                return;
            }

            long ID = generateID(name, description, date);
            Task task = new Task(ID, name, description, date);
            viewModel.OfflineSaveData(task);
        });
    }

    private void Online() {
        viewModel.getFailure().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Snackbar.make(binding.getRoot(), R.string.snackbar_InternetError, Snackbar.LENGTH_SHORT).show();
            }
        });

        binding.AddTask.setOnClickListener(v->{
            String name = binding.TaskName.getText().toString();
            String description = binding.TaskDescription.getText().toString();
            String date = getDate();

            if(name.isEmpty()) {
                Snackbar.make(binding.getRoot(), R.string.snackbar_EnterName, Snackbar.LENGTH_SHORT).show();
                return;
            }
            if(date.isEmpty()) {
                Snackbar.make(binding.getRoot(), R.string.snackbar_EnterDate, Snackbar.LENGTH_SHORT).show();
                return;
            }

            long ID = generateID(name, description, date);
            Task task = new Task(ID, name, description, date);
            viewModel.OnlineSaveData(task, token);
        });
    }

    private String getDate() {
        String day = binding.day.getText().toString();
        String month = binding.month.getText().toString();
        String year = binding.year.getText().toString();
        String minute = binding.minute.getText().toString();
        String hour = binding.hour.getText().toString();

        return day + '.' + month + '.' + year + ' ' + minute + ':' + hour;
    }

    private long generateID(String name, String Description, String date) {
        Random rand = new Random();

        long key1 = name.hashCode();
        long key2 = Description.hashCode();
        long key3 = date.hashCode();
        long key4 = rand.nextInt();

        long hash = key1 ^ key2 ^ key3 ^ key4;
        return hash;
    }

    public static Intent getIntent(Context context, String token) {
        Intent intent = new Intent(context, AddNewTask.class);
        intent.putExtra(TOKEN_KEY, token);
        return intent;
    }
}
package com.example.tasktracker.Activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.tasktracker.Activities.ViewModels.EditTaskViewModel;
import com.example.tasktracker.Answers.Task;
import com.example.tasktracker.PublicKeyNames;
import com.example.tasktracker.R;
import com.example.tasktracker.databinding.ActivityEditTaskBinding;
import com.google.android.material.snackbar.Snackbar;

public class EditTask extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "EDIT_TASK";
    private static final String TASK_ID_KEY = "TASK_ID";

    private ActivityEditTaskBinding binding;
    private EditTaskViewModel viewModel;
    private String token;
    private long taskID = -1;
    private String taskDateString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        viewModel = new EditTaskViewModel(getApplication());
        binding = ActivityEditTaskBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        taskID = intent.getLongExtra(TASK_ID_KEY, -1);
        if(taskID == -1) {
            Log.w(TAG, "No task id");
            return;
        }

        binding.btnDatePick.setOnClickListener(v->{ // <-----
            DatePickerFragment fragment;
            fragment = new DatePickerFragment();
            fragment.show(getSupportFragmentManager(), "DATE PICK");
        });

        viewModel.isLoading().observe(this, new Observer<Task>() {
            @Override
            public void onChanged(Task task) {
                binding.TaskName.setText(task.getTaskName());
                binding.TaskDescription.setText(task.getTaskDescription());
                taskDateString = task.getTaskDate();
                binding.btnDatePick.setText(taskDateString);
            }
        });
        viewModel.getClose().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean)
                    finish();
            }
        });

        token = intent.getStringExtra(PublicKeyNames.TOKEN_KEY);
        if(token == null)
        {
            Log.w(TAG, "Token is null");
            return;
        }

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
        viewModel.LoadTaskOffline(taskID, this);
        binding.btnEditTask.setOnClickListener(v->{
            String name = binding.TaskName.getText().toString();
            String description = binding.TaskDescription.getText().toString();

            Task task = new Task(taskID, name, description, taskDateString);
            viewModel.OfflineSaveData(task);
        });
    }

    private void Online() {
        binding.btnEditTask.setOnClickListener(v->{
            String name = binding.TaskName.getText().toString();
            String description = binding.TaskDescription.getText().toString();

            Task task = new Task(taskID, name, description, taskDateString);
            viewModel.OnlineSaveData(task, token);
        });
        viewModel.getLiveDataFail().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Snackbar.make(binding.getRoot(), R.string.snackbar_InternetError, Snackbar.LENGTH_LONG).show();
                Log.w(TAG, s);
            }
        });

        viewModel.LoadTaskOnline(token, taskID);
    }

    public static Intent getIntent(Context context, String token, long taskID) {
        Intent intent = new Intent(context, EditTask.class);
        intent.putExtra(PublicKeyNames.TOKEN_KEY, token);
        intent.putExtra(TASK_ID_KEY, taskID);
        return intent;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        taskDateString = Integer.toString(day) + '.'
                       + Integer.toString(month) + '.'
                       + Integer.toString(year);
        binding.btnDatePick.setText(taskDateString);
    }
}
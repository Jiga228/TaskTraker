package com.samsungschool.tasktracker.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.samsungschool.tasktracker.Activities.ViewModels.EditTaskViewModel;
import com.samsungschool.tasktracker.Activities.ViewModels.TimePickerFragment;
import com.samsungschool.tasktracker.Answers.Task;
import com.samsungschool.tasktracker.PublicKeyNames;
import com.samsungschool.tasktracker.R;
import com.samsungschool.tasktracker.databinding.ActivityEditTaskBinding;
import com.google.android.material.snackbar.Snackbar;

public class EditTask extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "EDIT_TASK";
    private static final String TASK_ID_KEY = "TASK_ID";

    private ActivityEditTaskBinding binding;
    private EditTaskViewModel viewModel;
    private String login, password;
    private long taskID = -1;
    private String taskDateString;
    private String taskTimeString;

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

        binding.btnDatePick.setOnClickListener(v->{
            DatePickerFragment fragment = new DatePickerFragment();
            fragment.show(getSupportFragmentManager(), "DATE PICK");
        });
        binding.btnTimePick.setOnClickListener(v->{
            TimePickerFragment fragment = new TimePickerFragment();
            fragment.show(getSupportFragmentManager(), "TIME PICKER");
        });

        viewModel.isLoading().observe(this, new Observer<Task>() {
            @Override
            public void onChanged(Task task) {
                binding.TaskName.setText(task.getTaskName());
                binding.TaskDescription.setText(task.getTaskDescription());
                taskDateString = task.getTaskDate();
                taskTimeString = task.getTaskTime();
                if(taskDateString == null) {
                    binding.btnDatePick.setText(R.string.button_DefaultData);
                }
                else {
                    binding.btnDatePick.setText(taskDateString);
                }
                if(taskTimeString == null) {
                    binding.btnTimePick.setText(R.string.button_DefaultTime);
                }
                else {
                    binding.btnTimePick.setText(taskTimeString);
                }
            }
        });
        viewModel.getClose().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean)
                    finish();
            }
        });

        login = intent.getStringExtra(PublicKeyNames.LOGIN_KEY);
        password = intent.getStringExtra(PublicKeyNames.PASSWORD_KEY);

        // Offline
        if(login == null || password == null) {
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

            Task task = new Task(taskID, taskDateString, name, description, taskTimeString);
            viewModel.OfflineSaveData(task);
        });
    }

    private void Online() {
        binding.btnEditTask.setOnClickListener(v->{
            String name = binding.TaskName.getText().toString();
            String description = binding.TaskDescription.getText().toString();

            Task task = new Task(taskID, taskDateString, name, description, taskTimeString);
            viewModel.OnlineSaveData(task, login, password);
        });
        viewModel.getLiveDataFail().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Snackbar.make(binding.getRoot(), R.string.snackbar_InternetError, Snackbar.LENGTH_LONG).show();
                Log.w(TAG, s);
            }
        });

        viewModel.LoadTaskOnline(login, password, taskID);
    }

    public static Intent getIntent(Context context, String login, String password, long taskID) {
        Intent intent = new Intent(context, EditTask.class);
        intent.putExtra(PublicKeyNames.LOGIN_KEY, login);
        intent.putExtra(PublicKeyNames.PASSWORD_KEY, password);
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

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        taskTimeString = Integer.toString(hour) + ':' + Integer.toString(minute);
        binding.btnTimePick.setText(taskTimeString);
    }
}
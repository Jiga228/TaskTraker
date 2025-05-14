package com.example.tasktracker.Activities;

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
import androidx.lifecycle.ViewModelProvider;

import com.example.tasktracker.Activities.ViewModels.AddItemViewModel;
import com.example.tasktracker.Activities.ViewModels.TimePickerFragment;
import com.example.tasktracker.Answers.Task;
import com.example.tasktracker.PublicKeyNames;
import com.example.tasktracker.R;
import com.example.tasktracker.databinding.ActivityAddNewTaskBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Random;

public class AddNewTask extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "ADD_NEW_TASK";

    private ActivityAddNewTaskBinding binding;
    private static String token;
    private AddItemViewModel viewModel;
    private String stringDate = "";
    private String stringTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityAddNewTaskBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        token = getIntent().getStringExtra(PublicKeyNames.TOKEN_KEY);
        if(token == null)
        {
            Log.w(TAG, "Token is null");
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

            if(name.isEmpty()) {
                Snackbar.make(binding.getRoot(), R.string.snackbar_EnterName, Snackbar.LENGTH_SHORT).show();
                return;
            }
            if(stringDate.isEmpty()) {
                Snackbar.make(binding.getRoot(), R.string.snackbar_EnterDate, Snackbar.LENGTH_SHORT).show();
                return;
            }
            if(stringTime.isEmpty()) {
                Snackbar.make(binding.getRoot(), R.string.snackbar_EnterDate, Snackbar.LENGTH_SHORT).show();
                return;
            }

            String dateTime = stringDate + ' ' + stringTime;

            long ID = generateID(name, description, dateTime);
            Task task = new Task(ID, name, description, dateTime);
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


            if(name.isEmpty()) {
                Snackbar.make(binding.getRoot(), R.string.snackbar_EnterName, Snackbar.LENGTH_SHORT).show();
                return;
            }
            if(stringDate.isEmpty()) {
                Snackbar.make(binding.getRoot(), R.string.snackbar_EnterDate, Snackbar.LENGTH_SHORT).show();
                return;
            }
            if(stringTime.isEmpty()) {
                Snackbar.make(binding.getRoot(), R.string.snackbar_EnterDate, Snackbar.LENGTH_SHORT).show();
                return;
            }

            String dateTime = stringDate + ' ' + stringTime;

            long ID = generateID(name, description, dateTime);
            Task task = new Task(ID, name, description, dateTime);
            viewModel.OnlineSaveData(task, token);
        });
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
        intent.putExtra(PublicKeyNames.TOKEN_KEY, token);
        return intent;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        stringDate = Integer.toString(day) + '.'
                   + Integer.toString(month) + '.'
                   + Integer.toString(year);
        binding.btnDatePick.setText(stringDate);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        stringTime = Integer.toString(hour) + ':' + Integer.toString(minute);
        binding.btnTimePick.setText(stringTime);
    }
}
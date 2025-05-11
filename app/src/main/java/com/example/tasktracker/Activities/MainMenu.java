package com.example.tasktracker.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasktracker.Answers.Task;
import com.example.tasktracker.Api.ApiFactory;
import com.example.tasktracker.Api.TaskRepository;
import com.example.tasktracker.R;
import com.example.tasktracker.databinding.ActivityMainMenuBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainMenu extends AppCompatActivity {
    private static final String TAG = "MainMenu";
    private static final String TOKEN_KEY = "TOKEN";

    private ActivityMainMenuBinding binding;
    private String token;
    private TaskAdapter taskAdapter;
    private MainMenuViewModel viewModel;

    private List<Task> taskListCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        token = getIntent().getStringExtra(TOKEN_KEY);
        if(token == null)
        {
            Log.e(TAG, "Token is empty");
            return;
        }
        taskAdapter = new TaskAdapter();
        binding.scrollTask.setAdapter(taskAdapter);

        binding.buttonAddNewTask.setOnClickListener(v->{
            Intent intent = AddNewTask.getIntent(this, token);
            startActivity(intent);
        });
        viewModel = new ViewModelProvider(this).get(MainMenuViewModel.class);

        binding.findLine.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String find = binding.findLine.getText().toString();
                if(find.isEmpty()) {
                    if(token.equals("null")) {
                        TaskRepository.newInstance(getApplication()).taskDao().getTaskList().observe(MainMenu.this, new Observer<List<Task>>() {
                            @Override
                            public void onChanged(List<Task> tasks) {
                                taskListCache = tasks;
                                taskAdapter.setTaskList(tasks);
                            }
                        });
                    }
                    else {
                        viewModel.OnlineUpdateList(token);
                    }
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<Task> searchList = new ArrayList<>();
                        for (int i = 0; i < taskListCache.size(); i++) {
                            if (taskListCache.get(i).getTaskName().toLowerCase().contains(charSequence) ||
                                    taskListCache.get(i).getTaskDescription().toLowerCase().contains(charSequence)) {
                                searchList.add(taskListCache.get(i));
                            }
                        }
                        new Handler(getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                taskAdapter.setTaskList(searchList);
                            }
                        });
                    }
                }).start();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // offline
        if(token.equals("null")) {
            Offline();
        }
        //online
        else {
            Online();
        }
    }

    public void Offline() {
        viewModel.getOnLoad().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                taskListCache = tasks;
                taskAdapter.setTaskList(tasks);
            }
        });

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                Task task = taskAdapter.getItem(position);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        TaskRepository.newInstance(getApplication()).taskDao().delete(task);
                    }
                }).start();
                taskAdapter.removeItemByPosition(position);
            }
        });
        helper.attachToRecyclerView(binding.scrollTask);
    }

    private void Online() {

        viewModel.getOnLoad().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                taskListCache = tasks;
                taskAdapter.setTaskList(tasks);
            }
        });
        viewModel.onFail().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Snackbar.make(binding.getRoot(), R.string.snackbar_InternetError, Snackbar.LENGTH_LONG).show();
                Log.w(TAG, s);
            }
        });

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                Task task = taskAdapter.getItem(position);
                ApiFactory.getApiService().removeTask(token, task.getID()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        Task task = taskAdapter.getItem(position);
                        taskAdapter.removeItemByPosition(position);
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable throwable) {
                        Snackbar.make(binding.getRoot(), R.string.snackbar_InternetError, Snackbar.LENGTH_LONG).show();
                        Log.w(TAG, Objects.requireNonNull(throwable.getMessage()));
                    }
                });
            }
        });
        helper.attachToRecyclerView(binding.scrollTask);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(token.equals("null")) {
            TaskRepository.newInstance(getApplication()).taskDao().getTaskList().observe(this, new Observer<List<Task>>() {
                @Override
                public void onChanged(List<Task> tasks) {
                    taskListCache = tasks;
                    taskAdapter.setTaskList(tasks);
                }
            });
        }
        else {
            viewModel.OnlineUpdateList(token);
        }
    }

    public static Intent getIntent(Context context, String token) {
        Intent intent = new Intent(context, MainMenu.class);
        intent.putExtra(TOKEN_KEY, token);
        return intent;
    }

}
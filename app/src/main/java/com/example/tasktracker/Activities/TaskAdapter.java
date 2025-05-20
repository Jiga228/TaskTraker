package com.example.tasktracker.Activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasktracker.Answers.Task;
import com.example.tasktracker.R;
import com.example.tasktracker.databinding.ItemBinding;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> taskList = new ArrayList<>();

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(@NonNull List<Task> taskList)
    {
        this.taskList = taskList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);

        return new TaskViewHolder(ItemBinding.bind(view));
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.binding.TaskName.setText(task.getTaskName());
        holder.binding.TaskDescription.setText(task.getTaskDescription());
        holder.binding.TaskDate.setText(task.getTaskDate() + ' ' + task.getTaskTime());
        holder.binding.getRoot().setOnClickListener(v->{
            if(onItemClick != null)
                onItemClick.Click(position);
        });
    }

    interface OnItemClick {
        void Click(int position);
    }
    OnItemClick onItemClick;
    void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    Task getItem(int position) {
        return taskList.get(position);
    }

    void removeItemByPosition(int position)
    {
        taskList.remove(position);
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        ItemBinding binding;
        TaskViewHolder(ItemBinding binding)
        {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

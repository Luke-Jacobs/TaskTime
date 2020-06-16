package com.example.timemanage;

import android.app.AlertDialog;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class TaskCustomAdapter extends RecyclerView.Adapter<TaskCustomAdapter.TaskViewHolder> {

    private ArrayList<Task> dataset;
    private TextView noTaskTextView;
    private int expandedPosition = -1;

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_card, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TaskViewHolder holder, final int position) {
        TextView taskNameTextView = holder.taskNameTextView;
        TextView taskDescriptionTextView = holder.taskDescriptionTextView;

        Task item = dataset.get(position);
        if (item != null) {
            taskNameTextView.setText(dataset.get(position).getName());
            taskDescriptionTextView.setText(dataset.get(position).getDescription());
        }

        // If this is an expanded card view
        if (expandedPosition == position) {
            taskDescriptionTextView.setEllipsize(null);  // Turn off ellipsis
            taskDescriptionTextView.setMaxLines(Integer.MAX_VALUE);
        }
    }

    void setExpandedCard(int position) {
        expandedPosition = position;
        notifyItemChanged(position);
    }

    void setFinishedTask(int position) {
        Task task = dataset.get(position);
        assert task != null;
        task.setFinished();
        notifyItemChanged(position);
    }

    private void sortUpdatedDataset() {
        // Sorts the internal dataset of this adapter
        Collections.sort(dataset, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                if (o1.getToCompleteByTime().isBefore(o2.getToCompleteByTime())) {
                    return 0;
                } else if (o1.getToCompleteByTime().equals(o2.getToCompleteByTime())) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }

    void addTask(Task newTask) {
        dataset.add(newTask);
        noTaskTextView.setVisibility(View.GONE);
        sortUpdatedDataset();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    TaskCustomAdapter(ArrayList<Task> dataset, TextView noTaskTextView) {
        this.dataset = dataset;
        this.noTaskTextView = noTaskTextView;
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskNameTextView;
        TextView taskDescriptionTextView;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            this.taskNameTextView = itemView.findViewById(R.id.taskNameText);
            this.taskDescriptionTextView = itemView.findViewById(R.id.taskDescriptionText);
        }
    }

}

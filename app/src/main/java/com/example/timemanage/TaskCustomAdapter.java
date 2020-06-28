package com.example.timemanage;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.threeten.bp.LocalDateTime;

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

    @SuppressLint("DefaultLocale")
    static void inflateTaskHolder(final TaskViewHolder holder, Task item, boolean expandCard) {
        // Fill text from Task data representation
        holder.taskNameTextView.setText(item.getName());
        holder.taskDescriptionTextView.setText(item.getDescription());

        // Fill due date text and color the text according to its urgency
        LocalDateTime taskDueDate = item.getToCompleteByTime();
        holder.dueDateTextView.setText(String.format("%s\n%d",
                taskDueDate.getMonth().toString(), taskDueDate.getDayOfMonth()));

        // If this task is finished
        if (item.isFinished()) {
            holder.greenBackground.setVisibility(View.VISIBLE);
            holder.checkMark.setVisibility(View.VISIBLE);
        } else {
            holder.greenBackground.setVisibility(View.GONE);
            holder.checkMark.setVisibility(View.GONE);
        }

        // If this is an expanded card view - TODO Make more efficient?
        if (expandCard) {
            holder.taskDescriptionTextView.setEllipsize(null);  // Turn off ellipsis
            holder.taskDescriptionTextView.setMaxLines(Integer.MAX_VALUE);
        } else {
            holder.taskDescriptionTextView.setEllipsize(TextUtils.TruncateAt.END);
            holder.taskDescriptionTextView.setMaxLines(2);
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull final TaskViewHolder holder, final int position) {
        Task item = dataset.get(position);
        assert item != null;

        boolean shouldExpand = (position == expandedPosition);
        inflateTaskHolder(holder, item, shouldExpand);
    }

    void setExpandedCard(int position) {
        int previousPosition = expandedPosition;
        expandedPosition = position;
        notifyItemChanged(position);
        notifyItemChanged(previousPosition);
    }

    void toggleFinishedTask(int position) {
        Task task = dataset.get(position);
        assert task != null;

        if (task.isFinished()) {
            task.setFinished(false);
        } else {
            task.setFinished(true);
        }

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
        TextView dueDateTextView;
        ImageView greenBackground;
        ImageView checkMark;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            this.taskNameTextView = itemView.findViewById(R.id.taskNameText);
            this.taskDescriptionTextView = itemView.findViewById(R.id.taskDescriptionText);
            this.dueDateTextView = itemView.findViewById(R.id.dueDateTextView);

            // Task completed views
            this.greenBackground = itemView.findViewById(R.id.greenBackground);
            this.checkMark = itemView.findViewById(R.id.checkMark);
        }
    }

}

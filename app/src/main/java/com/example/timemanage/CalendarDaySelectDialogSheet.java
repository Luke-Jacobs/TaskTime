package com.example.timemanage;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class CalendarDaySelectDialogSheet extends BottomSheetDialogFragment {

    private Task[] showTasks;

    CalendarDaySelectDialogSheet(Task[] showTasks) {
        super();
        this.showTasks = showTasks;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Populate view with that selected day's tasks
        LinearLayout sheetLayout = (LinearLayout) inflater.inflate(R.layout.calendar_select_day_sheet,
                container, false);

        if (showTasks.length == 0) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int marginAll = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
            lp.setMargins(marginAll, marginAll, marginAll, marginAll);
            sheetLayout.setLayoutParams(lp);

            TextView noTasksOnThisDayText = new TextView(getContext());
            noTasksOnThisDayText.setText(R.string.no_tasks_text);
            noTasksOnThisDayText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            sheetLayout.addView(noTasksOnThisDayText);
            return sheetLayout;
        }

        for (Task task: showTasks) {
            @SuppressLint("InflateParams") MaterialCardView thisTaskCardLayout = (MaterialCardView)
                    inflater.inflate(R.layout.task_card, null);
            TaskCustomAdapter.TaskViewHolder taskViewHolder =
                    new TaskCustomAdapter.TaskViewHolder(thisTaskCardLayout);
            TaskCustomAdapter.inflateTaskHolder(taskViewHolder, task, true);
            sheetLayout.addView(thisTaskCardLayout);
        }

        return sheetLayout;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
}

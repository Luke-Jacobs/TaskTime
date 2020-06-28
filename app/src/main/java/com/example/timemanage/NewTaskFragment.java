package com.example.timemanage;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;


public class NewTaskFragment extends Fragment implements
        CalendarView.OnDateChangeListener, TimePicker.OnTimeChangedListener {

    //Container Activity
    private MainActivity mainActivity;
    //Views
    private EditText taskNameET, taskDescriptionET;
    private LocalDate taskDueDate;
    private LocalTime taskDueTime;
    //Time Constants
    private LocalTime BEFORE_MIDNIGHT = LocalTime.of(11, 59, 59);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Flesh out UI
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) Objects.requireNonNull(getActivity());

        taskDueDate = LocalDate.now();
        taskDueTime = LocalTime.of(23, 59);

        Log.d("NewTaskFragment", "Starting new task activity");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d("NewTaskFragment", "onCreateView function");

        final View view = inflater.inflate(R.layout.new_task_options, container, false);

//        (Objects.requireNonNull(mainActivity.getActionBar())).setDisplayHomeAsUpEnabled(true);

        taskNameET = view.findViewById(R.id.taskName);
        taskDescriptionET = view.findViewById(R.id.taskDescription);

        // Set fragment button onClick listener
        view.findViewById(R.id.submitNewTask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitNewTask(v);
            }
        });

        // Set CalendarView time and listener
        CalendarView calendarView = view.findViewById(R.id.selectDueDateView);
        calendarView.setDate(System.currentTimeMillis());
        calendarView.setOnDateChangeListener((CalendarView.OnDateChangeListener) this);

        // Set TimeView time and listener
        TimePicker timePicker = view.findViewById(R.id.selectDueTimeView);
        timePicker.setCurrentHour(23);  // The default due time is always midnight
        timePicker.setCurrentMinute(59);
        timePicker.setOnTimeChangedListener(this);

        return view;
    }

    // Respond to the user clicking the "Finish Task" button
    // - This builds a Task object if all the necessary required fields are filled in
    private void onSubmitNewTask(View view) {
        //Fields to be filled out and sent back to the main activity
        String taskNameStr = taskNameET.getText().toString();
        String taskDescriptionStr = taskDescriptionET.getText().toString();
        if (taskDueTime == null) {
            taskDueTime = BEFORE_MIDNIGHT;
        }

        if (taskNameStr.compareTo("") != 0 && taskDescriptionStr.compareTo("") != 0 && taskDueDate != null) {
            // If all these fields are filled out
            LocalDateTime dt = LocalDateTime.of(taskDueDate, taskDueTime);
            Task t = new Task(taskNameStr, taskDescriptionStr, dt);
            Log.d("Returning Task", t.getStrRepr());

            // Return to the MainActivity with the new task fields that we collected from the user
            Intent returningData = new Intent();
            returningData.putExtra("returnedTask", t);
            mainActivity.onNewTaskFragmentReturned(t);

        } else {
            // If the user did not fill out an essential field to create a new task
            String missingItemStr;
            if (taskNameStr.compareTo("") == 0) {
                missingItemStr = "task name";
            } else if (taskDescriptionStr.compareTo("") == 0) {
                missingItemStr = "task description";
            } else if (taskDueDate == null) {
                missingItemStr = "task due date";
            } else {
                missingItemStr = "(unknown)";
            }

            new AlertDialog.Builder(mainActivity)
                    .setTitle("Missing fields")
                    .setMessage(String.format("You are missing the %s", missingItemStr))
                    .show();
        }
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        // When the user selects the due time
        LocalTime selectedTimeToReturn = LocalTime.of(hourOfDay, minute);
        Log.d("NewTaskFragment",
                String.format("User selected: %s", selectedTimeToReturn.toString()));
        taskDueTime = selectedTimeToReturn;
    }

    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
        // When the user selects the due date
        LocalDate selectedDateToReturn = LocalDate.of(year, month, dayOfMonth);
        Log.d("NewTaskFragment",
                String.format("User selected: %s", selectedDateToReturn.toString()));
        taskDueDate = selectedDateToReturn;
    }

}

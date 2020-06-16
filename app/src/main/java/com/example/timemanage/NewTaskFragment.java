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

import com.google.android.material.datepicker.MaterialDatePicker;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.Objects;


public class NewTaskFragment extends Fragment {

    //Container Activity
    private MainActivity mainActivity;
    //Views
    private EditText taskNameET, taskDescriptionET;
    private TextView taskDueDateSelectionTV, taskDueTimeSelectionTV;
    //Fields to be filled out and sent back to the main activity
    private String taskNameStr, taskDescriptionStr;
    private LocalDate taskDueDate;
    private LocalTime taskDueTime;
    //Time Constants
    private LocalTime BEFORE_MIDNIGHT = LocalTime.of(11, 59, 59);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Flesh out UI
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) Objects.requireNonNull(getActivity());

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
        taskDueDateSelectionTV = view.findViewById(R.id.taskDueDateTextView);
        taskDueTimeSelectionTV = view.findViewById(R.id.taskDueTimeTextView);

        // Set fragment button onClick listener
        view.findViewById(R.id.submitNewTask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitNewTask(v);
            }
        });

        Button openDueDatePickerBtn = view.findViewById(R.id.openDueDatePickerBtn);
        openDueDatePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle "Select Due Date" button click
                AppCompatDialogFragment dueDatePickerFragment = new TaskDatePickerFragment();
                dueDatePickerFragment.setTargetFragment(NewTaskFragment.this, 0);
                dueDatePickerFragment.show(mainActivity.getSupportFragmentManager(), "dueDatePickerFragment");
            }
        });

        Button openDueTimePickerBtn = view.findViewById(R.id.openDueTimeButton);
        openDueTimePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatDialogFragment dueTimePickerFragment = new TaskTimePickerFragment();
                dueTimePickerFragment.setTargetFragment(NewTaskFragment.this, 0);
                dueTimePickerFragment.show(mainActivity.getSupportFragmentManager(), "dueTimePickerFragment");
            }
        });

        return view;
    }

    private void taskDueDateUpdated(LocalDate selectedTimeToReturn) {
        taskDueDate = selectedTimeToReturn;
        String updatedDueDateStr = taskDueDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        taskDueDateSelectionTV.setText(String.format("Due Date: %s", updatedDueDateStr));
        taskDueDateSelectionTV.setVisibility(View.VISIBLE);
    }

    private void taskDueTimeUpdated(LocalTime selectedTimeToReturn) {
        taskDueTime = selectedTimeToReturn;
        String updatedDueDateStr = taskDueTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        taskDueTimeSelectionTV.setText(String.format("Due Time: %s", updatedDueDateStr));
        taskDueTimeSelectionTV.setVisibility(View.VISIBLE);
    }

    // Respond to the user clicking the "Finish Task" button
    // - This builds a Task object if all the necessary required fields are filled in
    private void onSubmitNewTask(View view) {
        taskNameStr = taskNameET.getText().toString();
        taskDescriptionStr = taskDescriptionET.getText().toString();
        if (taskDueTime == null) {
            taskDueTime = BEFORE_MIDNIGHT;
        }

        if (taskNameStr.compareTo("") != 0 && taskDescriptionStr.compareTo("") != 0 && taskDueDate != null) {
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


    /*
     *
     * This fragment is used to be the pop-up the users see when they click on the select due date
     * button.
     *
     * */

    public static class TaskDatePickerFragment extends AppCompatDialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            LocalDate selectedDateToReturn = LocalDate.of(year, month, dayOfMonth);
            Log.d("TaskDatePickerFragment",
                    String.format("User selected: %s", selectedDateToReturn.toString()));
            NewTaskFragment hostActivity = (NewTaskFragment) Objects.requireNonNull(getTargetFragment());
            hostActivity.taskDueDateUpdated(selectedDateToReturn);
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            super.onCreateDialog(savedInstanceState);

            // The current date will be the starting date
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            Log.d("TaskDatePickerFragment", "Starting date picker dialog window");

            return new DatePickerDialog(Objects.requireNonNull(getActivity()),
                    this, year, month, day);
        }
    }

    /*
    *
    * This fragment is like the above, but for selecting the due time.
    *
    * */

    public static class TaskTimePickerFragment extends AppCompatDialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            super.onCreateDialog(savedInstanceState);

            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            Log.d("TaskDatePickerFragment", "Starting date picker dialog window");

            return new TimePickerDialog(Objects.requireNonNull(getActivity()),
                    this, hour, minute, true);
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            LocalTime selectedTimeToReturn = LocalTime.of(hourOfDay, minute);
            Log.d("TaskDatePickerFragment",
                    String.format("User selected: %s", selectedTimeToReturn.toString()));
            NewTaskFragment hostActivity = ((NewTaskFragment) Objects.requireNonNull(getTargetFragment()));
            hostActivity.taskDueTimeUpdated(selectedTimeToReturn);
        }
    }
}

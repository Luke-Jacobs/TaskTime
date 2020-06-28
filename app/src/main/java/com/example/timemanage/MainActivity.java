package com.example.timemanage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.LocalDateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Logic variables
    String TASKS_FILENAME = "tasks.bin";
    ArrayList<Task> tasks;  //Current tasks

    //Fragments
    private TaskListViewFragment taskListViewFragment = new TaskListViewFragment();
    private NewTaskFragment newTaskFragment = new NewTaskFragment();
    private CalendarFragment calendarFragment = new CalendarFragment();
    int currentFragmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_frame);

        AndroidThreeTen.init(this);

        //Load task cards
        tasks = loadStoredTasks();
        if (tasks == null) {
            tasks = new ArrayList<>();
        }
        // DEBUG
        tasks = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            tasks.add(new Task("Name", "This is a very lengthy description that " +
                    "should allocate more than a few lines on my tiny phone screen. This will be " +
                    "the real test of my programming skills.",
                    LocalDateTime.of(2020, 5, 23, 17, 50)));
        }
        // Bottom Navigation Menu Setup
        BottomNavigationView navigationView = findViewById(R.id.bottomNav);
        navigationView.setOnNavigationItemSelectedListener(new bottomNavigationSwitcher());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_container_1, newTaskFragment, "newTaskFragment");
        ft.add(R.id.fragment_container_2, taskListViewFragment, "taskListViewFragment");
        ft.add(R.id.fragment_container_3, calendarFragment, "calendarFragment");
        ft.hide(newTaskFragment);
        ft.hide(calendarFragment);
        ft.commit();

        Log.d("MainActivity", "Finished MainActivity creation! :)");
    }

    class bottomNavigationSwitcher implements BottomNavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int itemId = item.getItemId();

            // If item is already showing
            if (itemId == currentFragmentId) {
                return true;
            }

            // If we need to replace the currently-showing fragment
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            switch (itemId) {
                case (R.id.bottomNavTasksId):
                    ft.hide(newTaskFragment);
                    ft.hide(calendarFragment);
                    ft.show(taskListViewFragment);
                    break;
                case (R.id.bottomNavNewId):
                    ft.hide(calendarFragment);
                    ft.hide(taskListViewFragment);
                    ft.show(newTaskFragment);
                    break;
                case (R.id.bottomNavCalendarId):
                    ft.hide(newTaskFragment);
                    ft.hide(taskListViewFragment);
                    ft.show(calendarFragment);
                    break;
            }
            ft.commit();

            return true;
        }

    }

    protected void onNewTaskFragmentReturned(Task returnedTask) {
        Log.d("MainActivity", "New task created");
        taskListViewFragment.adapter.addTask(returnedTask);
        calendarFragment.updateCalendarEventIcons();
    }

    protected void onTaskEdited() {
        calendarFragment.updateCalendarEventIcons();
    }

    // For initializing the tasks made by users in the past, stored in a file
    // Returns null if tasks could not be found
    private ArrayList<Task> loadStoredTasks() {
        Context context = getApplicationContext();
        try (FileInputStream fileInputStream = context.openFileInput(TASKS_FILENAME)) {

            // Read data from data file
            File f = new File(TASKS_FILENAME);
            if (!f.exists()) {
                Log.d("MainActivity", "Could not find a stored tasks file");
                return null;
            }
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            // Unpack Task objects
            return (ArrayList<Task>) objectInputStream.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // For retrieving tasks by a day key
    protected Task[] getTasksDueOnDay(LocalDateTime day) {
        ArrayList<Task> tasksOutput = new ArrayList<>();

        for (Task task: tasks) {
            if (task.getToCompleteByTime().toLocalDate().equals(day.toLocalDate())) {
                tasksOutput.add(task);
            }
        }

        if (tasksOutput.size() > 0) {
            return tasksOutput.toArray(new Task[0]);
        } else {
            return new Task[]{};
        }
    }

}


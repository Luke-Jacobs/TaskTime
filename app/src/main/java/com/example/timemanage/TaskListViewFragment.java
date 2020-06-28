package com.example.timemanage;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Objects;


public class TaskListViewFragment extends Fragment implements RecyclerView.OnItemTouchListener {

    private MainActivity mainActivity;

    // UI Variables
    private RecyclerView recyclerView;
    TaskCustomAdapter adapter;
    private GestureDetector gestureDetector;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = Objects.requireNonNull((MainActivity) getActivity());

        // Initialize Task Card Gesture Detection
        gestureDetector = new GestureDetector(mainActivity, new RecyclerViewGestureListener());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);


        View fragmentView = inflater.inflate(R.layout.tasks_layout, container, false);
        TextView noTasksTextView = fragmentView.findViewById(R.id.no_tasks_textview);
        adapter = new TaskCustomAdapter(mainActivity.tasks, noTasksTextView);

        //UI Variables
        recyclerView = fragmentView.findViewById(R.id.tasksRecyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mainActivity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(this);

        //No Tasks Message
        if (mainActivity.tasks.size() == 0) {
            noTasksTextView.setVisibility(View.VISIBLE);
        } else {
            noTasksTextView.setVisibility(View.GONE);
        }

        // Double click detection
        gestureDetector = new GestureDetector(mainActivity, new RecyclerViewGestureListener());
        return fragmentView;
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    private class RecyclerViewGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            super.onDoubleTap(e);
            View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
            assert view != null;

            // Finish this task
            int position = recyclerView.getChildAdapterPosition(view);
            adapter.toggleFinishedTask(position);
            Log.d("GestureListener",
                    String.format("Finishing task at %d", position));
            mainActivity.onTaskEdited();

            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            super.onSingleTapConfirmed(e);
            View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
            assert view != null;

            // Expand this card
            int position = recyclerView.getChildAdapterPosition(view);
            Log.d("GestureListener", String.format("Expanding %d", position));
            adapter.setExpandedCard(position);

            return true;
        }
    }

}

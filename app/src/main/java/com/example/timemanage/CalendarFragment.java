package com.example.timemanage;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CalendarFragment extends Fragment implements OnDayClickListener {

    private ArrayList<EventDay> eventData = new ArrayList<>();
    private MainActivity mainActivity;
    private CalendarView calendarView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) Objects.requireNonNull(getActivity());

        updateCalendarEventIcons();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_tab_layout, container, false);

        // Init calendarView
        calendarView = view.findViewById(R.id.calendarView);
        try {
            calendarView.setDate(new Date());
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }
        calendarView.setOnDayClickListener(this);
        calendarView.setEvents(eventData);

        return view;
    }

    void updateCalendarEventIcons() {
        eventData.clear();
        for (Task task: mainActivity.tasks) {
            int iconResource;
            // Set Calendar instance to the date stored in `task`
            LocalDateTime dueDate = task.getToCompleteByTime();
            Calendar c = Calendar.getInstance();
            c.set(dueDate.getYear(), dueDate.getMonthValue(), dueDate.getDayOfMonth());
            // Select the icon for this task
            if (task.isFinished()) {
                iconResource = R.drawable.ic_check_black_24dp;
            } else {
                iconResource = R.drawable.ic_book_black_24dp;
            }

            eventData.add(new EventDay(c, iconResource));
        }
        if (calendarView != null) {
            Log.d("[CalendarFragment]", "Events: \n");
            for (EventDay eventDay: eventData) {
                Log.d("[CalendarFragment]", String.format("\t<%s>", eventDay.getCalendar().toString()));
            }
            calendarView.setEvents(eventData);
        }
    }

    @Override
    public void onDayClick(EventDay eventDay) {
        /*
        * Responds to the user clicking a specific day on the CalendarView.
        * */
        Calendar calendar = eventDay.getCalendar();
        Task[] tasks = mainActivity.getTasksDueOnDay(
                LocalDateTime.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR),
                        calendar.get(Calendar.MINUTE)));
        assert getFragmentManager() != null;
        (new CalendarDaySelectDialogSheet(tasks)).show(getFragmentManager(), "[CalendarDaySelect]");
    }
}

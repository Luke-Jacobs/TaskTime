package com.example.timemanage;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;


class Task implements java.io.Serializable {
    private String name, description;
    private LocalDateTime toCompleteByTime;
    private boolean isFinished = false;

    Task(String name, String description, LocalDateTime toCompleteByTime) {
        this.name = name;
        this.description = description;
        this.toCompleteByTime = toCompleteByTime;
    }

    String getStrRepr() {
        return String.format("< Task | Name: %s | Desc: %s | Due: %s | Finished: %b>",
                name, description, getCompleteByTimeStr(), isFinished);
    }

    String getName() {
        return name;
    }

    String getDescription() {
        return description;
    }

    LocalDateTime getToCompleteByTime() {
        return toCompleteByTime;
    }

    boolean isFinished() {
        return isFinished;
    }

    void setFinished() {
        isFinished = true;
    }

    private String getCompleteByTimeStr() {
        return DateTimeFormatter.ofPattern("E, MMM dd, HH:mm").format(toCompleteByTime);
    }
}

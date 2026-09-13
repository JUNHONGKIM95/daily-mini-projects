package com.junhong.onething;

import android.content.Context;
import android.content.SharedPreferences;

import java.time.LocalDate;

public final class TodayStore {
    private static final String PREFS = "one_thing_today";
    private static final String KEY_DATE = "date";
    private static final String KEY_TEXT = "text";
    private static final String KEY_COMPLETED = "completed";

    private TodayStore() {}

    public static Task read(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String today = LocalDate.now().toString();
        if (!today.equals(preferences.getString(KEY_DATE, ""))) {
            return new Task("", false);
        }
        return new Task(preferences.getString(KEY_TEXT, ""), preferences.getBoolean(KEY_COMPLETED, false));
    }

    public static void save(Context context, String text) {
        String cleanText = text == null ? "" : text.trim().replaceAll("\\s+", " ");
        if (cleanText.length() > 80) cleanText = cleanText.substring(0, 80);
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_DATE, LocalDate.now().toString())
                .putString(KEY_TEXT, cleanText)
                .putBoolean(KEY_COMPLETED, false)
                .apply();
    }

    public static void toggle(Context context) {
        Task task = read(context);
        if (task.text.isEmpty()) return;
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_DATE, LocalDate.now().toString())
                .putString(KEY_TEXT, task.text)
                .putBoolean(KEY_COMPLETED, !task.completed)
                .apply();
    }

    public static final class Task {
        public final String text;
        public final boolean completed;

        Task(String text, boolean completed) {
            this.text = text;
            this.completed = completed;
        }
    }
}


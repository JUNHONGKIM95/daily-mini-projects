package com.junhong.onething;

import android.content.Context;
import android.content.SharedPreferences;

import java.time.LocalDate;

public final class TodayStore {
    private static final String PREFS = "one_thing_today";
    private static final String KEY_DATE = "date";
    private static final String KEY_TEXT = "text";
    private static final String KEY_COMPLETED = "completed";
    private static final String TEXT_PREFIX = "text_";
    private static final String COMPLETED_PREFIX = "completed_";

    private TodayStore() {}

    public static Task read(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String today = LocalDate.now().toString();
        String textKey = TEXT_PREFIX + today;

        // Migrate the first APK's single-day storage without losing today's entry.
        if (!preferences.contains(textKey) && today.equals(preferences.getString(KEY_DATE, ""))) {
            preferences.edit()
                    .putString(textKey, preferences.getString(KEY_TEXT, ""))
                    .putBoolean(COMPLETED_PREFIX + today, preferences.getBoolean(KEY_COMPLETED, false))
                    .apply();
        }
        return new Task(preferences.getString(textKey, ""), preferences.getBoolean(COMPLETED_PREFIX + today, false));
    }

    public static void save(Context context, String text) {
        String cleanText = text == null ? "" : text.trim().replaceAll("\\s+", " ");
        if (cleanText.length() > 80) cleanText = cleanText.substring(0, 80);
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(TEXT_PREFIX + LocalDate.now(), cleanText)
                .putBoolean(COMPLETED_PREFIX + LocalDate.now(), false)
                .apply();
    }

    public static void toggle(Context context) {
        Task task = read(context);
        if (task.text.isEmpty()) return;
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(TEXT_PREFIX + LocalDate.now(), task.text)
                .putBoolean(COMPLETED_PREFIX + LocalDate.now(), !task.completed)
                .apply();
    }

    public static int stateFor(Context context, LocalDate date) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String key = date.toString();
        if (preferences.getString(TEXT_PREFIX + key, "").isEmpty()) return 0;
        return preferences.getBoolean(COMPLETED_PREFIX + key, false) ? 2 : 1;
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

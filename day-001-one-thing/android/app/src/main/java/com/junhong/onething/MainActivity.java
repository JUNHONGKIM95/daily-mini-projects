package com.junhong.onething;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MainActivity extends Activity {
    private EditText taskInput;
    private TextView counter;
    private CheckBox completeCheck;
    private LinearLayout taskFormPanel;
    private LinearLayout savedTaskPanel;
    private TextView encouragement;
    private TextView streakCopy;
    private LinearLayout recentWeek;
    private boolean editing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskInput = findViewById(R.id.task_input);
        counter = findViewById(R.id.character_counter);
        completeCheck = findViewById(R.id.saved_complete_check);
        taskFormPanel = findViewById(R.id.task_form_panel);
        savedTaskPanel = findViewById(R.id.saved_task_panel);
        encouragement = findViewById(R.id.encouragement);
        streakCopy = findViewById(R.id.streak_copy);
        recentWeek = findViewById(R.id.recent_week);
        Button saveButton = findViewById(R.id.save_button);
        Button editButton = findViewById(R.id.edit_button);
        TextView dateLabel = findViewById(R.id.date_label);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월 d일 EEEE", Locale.KOREAN);
        dateLabel.setText(LocalDate.now().format(formatter));

        taskInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                counter.setText(getString(R.string.character_count, s.length()));
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        saveButton.setOnClickListener(view -> saveTask());
        completeCheck.setOnClickListener(view -> {
            TodayStore.toggle(this);
            OneThingWidget.updateAll(this);
            render();
        });
        editButton.setOnClickListener(view -> {
            editing = true;
            render();
            taskInput.requestFocus();
        });
        render();
    }

    @Override
    protected void onResume() {
        super.onResume();
        render();
    }

    private void saveTask() {
        String text = taskInput.getText().toString().trim();
        if (text.isEmpty()) {
            taskInput.setError(getString(R.string.empty_task_error));
            return;
        }
        TodayStore.save(this, text);
        editing = false;
        OneThingWidget.updateAll(this);
        Toast.makeText(this, R.string.saved_message, Toast.LENGTH_SHORT).show();
        render();
    }

    private void render() {
        TodayStore.Task task = TodayStore.read(this);
        if (!task.text.equals(taskInput.getText().toString())) {
            taskInput.setText(task.text);
            taskInput.setSelection(taskInput.length());
        }
        boolean hasTask = !task.text.isEmpty();
        taskFormPanel.setVisibility(!hasTask || editing ? View.VISIBLE : View.GONE);
        savedTaskPanel.setVisibility(hasTask && !editing ? View.VISIBLE : View.GONE);
        completeCheck.setChecked(task.completed);
        completeCheck.setText(task.text);
        completeCheck.setPaintFlags(task.completed
                ? completeCheck.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
                : completeCheck.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        encouragement.setText(task.completed ? R.string.completed_label : R.string.focus_label);
        renderRecentWeek();
    }

    private void renderRecentWeek() {
        recentWeek.removeAllViews();
        int completedCount = 0;
        float density = getResources().getDisplayMetrics().density;

        for (int daysAgo = 6; daysAgo >= 0; daysAgo--) {
            LocalDate date = LocalDate.now().minusDays(daysAgo);
            int state = TodayStore.stateFor(this, date);
            if (state == 2) completedCount++;

            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setGravity(android.view.Gravity.CENTER);
            item.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            TextView day = new TextView(this);
            day.setText(date.format(DateTimeFormatter.ofPattern("EEEEE", Locale.KOREAN)));
            day.setTextSize(11);
            day.setTextColor(getColor(R.color.muted));
            day.setGravity(android.view.Gravity.CENTER);

            View dot = new View(this);
            int size = Math.round(12 * density);
            LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(size, size);
            dotParams.topMargin = Math.round(8 * density);
            dot.setLayoutParams(dotParams);
            dot.setBackgroundResource(state == 2
                    ? R.drawable.day_done
                    : state == 1 ? R.drawable.day_planned : R.drawable.day_empty);

            item.addView(day);
            item.addView(dot);
            recentWeek.addView(item);
        }

        streakCopy.setText(completedCount == 0
                ? getString(R.string.recent_empty)
                : getString(R.string.recent_count, completedCount));
    }
}

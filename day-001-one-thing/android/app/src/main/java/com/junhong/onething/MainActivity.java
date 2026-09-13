package com.junhong.onething;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MainActivity extends Activity {
    private EditText taskInput;
    private TextView counter;
    private CheckBox completeCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskInput = findViewById(R.id.task_input);
        counter = findViewById(R.id.character_counter);
        completeCheck = findViewById(R.id.complete_check);
        Button saveButton = findViewById(R.id.save_button);
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
        completeCheck.setVisibility(hasTask ? View.VISIBLE : View.GONE);
        completeCheck.setChecked(task.completed);
        completeCheck.setText(task.completed ? R.string.completed_label : R.string.complete_label);
    }
}


package com.junhong.thoughtparking;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

public class MainActivity extends Activity {
    private static final int NOTIFICATION_PERMISSION_REQUEST = 3001;
    private LinearLayout thoughtList;
    private TextView counter;
    private TextView emptyState;
    private EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        thoughtList = findViewById(R.id.thought_list);
        counter = findViewById(R.id.thought_count);
        emptyState = findViewById(R.id.empty_state);
        input = findViewById(R.id.thought_input);
        Button parkButton = findViewById(R.id.park_button);

        parkButton.setOnClickListener(view -> parkThought());
        input.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                parkThought();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderThoughts();
        ThoughtNotification.sync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ThoughtNotification.sync(this);
        }
    }

    private void parkThought() {
        String text = ThoughtText.normalize(input.getText().toString());
        if (text.isEmpty()) {
            input.setError(getString(R.string.empty_thought_error));
            input.requestFocus();
            return;
        }
        ThoughtStore.add(this, text);
        input.setText("");
        renderThoughts();
        ensureNotificationPermissionAndSync();
        Toast.makeText(this, R.string.parked_message, Toast.LENGTH_SHORT).show();
    }

    private void ensureNotificationPermissionAndSync() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST);
            return;
        }
        ThoughtNotification.sync(this);
    }

    private void renderThoughts() {
        List<ThoughtStore.Thought> thoughts = ThoughtStore.read(this);
        thoughtList.removeAllViews();
        counter.setText(getResources().getQuantityString(R.plurals.parked_count, thoughts.size(), thoughts.size()));
        emptyState.setVisibility(thoughts.isEmpty() ? View.VISIBLE : View.GONE);
        for (int index = 0; index < thoughts.size(); index++) {
            thoughtList.addView(createThoughtRow(thoughts.get(index), index + 1));
        }
    }

    private View createThoughtRow(ThoughtStore.Thought thought, int position) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(14), 0, dp(14));
        row.setBackgroundResource(R.drawable.list_item_border);

        TextView number = new TextView(this);
        number.setText(String.format("%02d", position));
        number.setTextColor(getColor(R.color.violet));
        number.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        row.addView(number, new LinearLayout.LayoutParams(dp(44), LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout copy = new LinearLayout(this);
        copy.setOrientation(LinearLayout.VERTICAL);
        TextView text = new TextView(this);
        text.setText(thought.text);
        text.setTextColor(getColor(R.color.ink));
        text.setTextSize(16);
        text.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        TextView time = new TextView(this);
        time.setText(DateFormat.getTimeFormat(this).format(new Date(thought.createdAt)));
        time.setTextColor(getColor(R.color.muted));
        time.setTextSize(11);
        time.setPadding(0, dp(5), 0, 0);
        copy.addView(text);
        copy.addView(time);
        row.addView(copy, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        Button resolve = new Button(this);
        resolve.setText("✓");
        resolve.setTextSize(16);
        resolve.setTextColor(getColor(R.color.violet));
        resolve.setBackgroundResource(R.drawable.resolve_button);
        resolve.setContentDescription(getString(R.string.resolve_description, thought.text));
        resolve.setOnClickListener(view -> {
            ThoughtStore.remove(this, thought.id);
            renderThoughts();
            ThoughtNotification.sync(this);
        });
        row.addView(resolve, new LinearLayout.LayoutParams(dp(44), dp(44)));
        return row;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}

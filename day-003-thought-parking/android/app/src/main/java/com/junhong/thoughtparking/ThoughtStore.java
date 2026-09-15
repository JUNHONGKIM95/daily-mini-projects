package com.junhong.thoughtparking;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ThoughtStore {
    private static final String PREFS = "thought_parking_store";
    private static final String KEY_THOUGHTS = "thoughts";
    private static final int MAX_THOUGHTS = 50;

    private ThoughtStore() {}

    public static List<Thought> read(Context context) {
        String raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(KEY_THOUGHTS, "[]");
        List<Thought> thoughts = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(raw);
            for (int index = 0; index < array.length() && thoughts.size() < MAX_THOUGHTS; index++) {
                JSONObject item = array.optJSONObject(index);
                if (item == null) continue;
                String id = item.optString("id", "");
                String text = ThoughtText.normalize(item.optString("text", ""));
                long createdAt = item.optLong("createdAt", 0L);
                if (!id.isEmpty() && !text.isEmpty() && createdAt > 0L) {
                    thoughts.add(new Thought(id, text, createdAt));
                }
            }
        } catch (JSONException ignored) {
            // A damaged local value is treated as an empty parking lot.
        }
        return thoughts;
    }

    public static void add(Context context, String text) {
        String clean = ThoughtText.normalize(text);
        if (clean.isEmpty()) return;
        List<Thought> thoughts = read(context);
        thoughts.add(0, new Thought(UUID.randomUUID().toString(), clean, System.currentTimeMillis()));
        if (thoughts.size() > MAX_THOUGHTS) thoughts = thoughts.subList(0, MAX_THOUGHTS);
        write(context, thoughts);
    }

    public static void remove(Context context, String id) {
        List<Thought> thoughts = read(context);
        thoughts.removeIf(thought -> thought.id.equals(id));
        write(context, thoughts);
    }

    public static void removeLatest(Context context) {
        List<Thought> thoughts = read(context);
        if (thoughts.isEmpty()) return;
        thoughts.remove(0);
        write(context, thoughts);
    }

    private static void write(Context context, List<Thought> thoughts) {
        JSONArray array = new JSONArray();
        for (Thought thought : thoughts) {
            JSONObject item = new JSONObject();
            try {
                item.put("id", thought.id);
                item.put("text", thought.text);
                item.put("createdAt", thought.createdAt);
                array.put(item);
            } catch (JSONException ignored) {
                // The values above are JSON-safe primitives.
            }
        }
        SharedPreferences preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        preferences.edit().putString(KEY_THOUGHTS, array.toString()).apply();
    }

    public static final class Thought {
        public final String id;
        public final String text;
        public final long createdAt;

        Thought(String id, String text, long createdAt) {
            this.id = id;
            this.text = text;
            this.createdAt = createdAt;
        }
    }
}

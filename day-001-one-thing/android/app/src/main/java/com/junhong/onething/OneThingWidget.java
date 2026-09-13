package com.junhong.onething;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.widget.RemoteViews;

public class OneThingWidget extends AppWidgetProvider {
    static final String ACTION_TOGGLE = "com.junhong.onething.TOGGLE_TASK";
    private static final int COMPACT_MAX_WIDTH_DP = 100;

    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            manager.updateAppWidget(appWidgetId, createViews(context, manager, appWidgetId));
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager manager, int appWidgetId, Bundle newOptions) {
        manager.updateAppWidget(appWidgetId, createViews(context, manager, appWidgetId));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_TOGGLE.equals(intent.getAction())) {
            TodayStore.toggle(context);
            updateAll(context);
            TodayNotification.sync(context);
        } else if (Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction())) {
            updateAll(context);
            TodayNotification.sync(context);
        } else if (Intent.ACTION_DATE_CHANGED.equals(intent.getAction())
                || Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction())) {
            updateAll(context);
            TodayNotification.sync(context);
        }
    }

    public static void updateAll(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName provider = new ComponentName(context, OneThingWidget.class);
        int[] ids = manager.getAppWidgetIds(provider);
        for (int id : ids) {
            manager.updateAppWidget(id, createViews(context, manager, id));
        }
    }

    private static RemoteViews createViews(Context context, AppWidgetManager manager, int appWidgetId) {
        TodayStore.Task task = TodayStore.read(context);
        boolean hasTask = !task.text.isEmpty();
        int width = manager.getAppWidgetOptions(appWidgetId)
                .getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 0);
        boolean compact = width == 0 || width < COMPACT_MAX_WIDTH_DP;
        int layout = compact ? R.layout.widget_one_thing_compact : R.layout.widget_one_thing;
        RemoteViews views = new RemoteViews(context.getPackageName(), layout);

        views.setInt(R.id.widget_root, "setBackgroundResource", task.completed
                ? R.drawable.widget_background
                : R.drawable.widget_background_pending);

        CharSequence taskText = hasTask ? task.text : context.getString(R.string.widget_empty_short);
        if (task.completed && hasTask) {
            SpannableString completedText = new SpannableString(task.text);
            completedText.setSpan(new StrikethroughSpan(), 0, task.text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            taskText = completedText;
        }
        views.setTextViewText(R.id.widget_task, taskText);
        views.setTextColor(R.id.widget_task, context.getColor(task.completed
                ? R.color.widget_muted
                : R.color.white));
        views.setImageViewResource(R.id.widget_toggle, task.completed
                ? R.drawable.widget_check_done
                : R.drawable.widget_check_pending);

        Intent openIntent = new Intent(context, MainActivity.class);
        PendingIntent openPending = PendingIntent.getActivity(context, 10_000 + appWidgetId, openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_root, openPending);

        if (hasTask) {
            Intent toggleIntent = new Intent(context, OneThingWidget.class).setAction(ACTION_TOGGLE);
            PendingIntent togglePending = PendingIntent.getBroadcast(context, appWidgetId, toggleIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_toggle, togglePending);
        }
        return views;
    }
}

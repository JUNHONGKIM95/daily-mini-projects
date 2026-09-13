package com.junhong.onething;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class OneThingWidget extends AppWidgetProvider {
    private static final String ACTION_TOGGLE = "com.junhong.onething.TOGGLE_TASK";

    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            manager.updateAppWidget(appWidgetId, createViews(context));
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_TOGGLE.equals(intent.getAction())) {
            TodayStore.toggle(context);
            updateAll(context);
        }
    }

    public static void updateAll(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName provider = new ComponentName(context, OneThingWidget.class);
        int[] ids = manager.getAppWidgetIds(provider);
        for (int id : ids) manager.updateAppWidget(id, createViews(context));
    }

    private static RemoteViews createViews(Context context) {
        TodayStore.Task task = TodayStore.read(context);
        boolean hasTask = !task.text.isEmpty();
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_one_thing);

        views.setTextViewText(R.id.widget_task, hasTask ? task.text : context.getString(R.string.widget_empty));
        views.setTextViewText(R.id.widget_toggle, task.completed ? "✓" : "○");
        views.setTextColor(R.id.widget_task, context.getColor(task.completed
                ? R.color.widget_muted
                : R.color.white));

        Intent openIntent = new Intent(context, MainActivity.class);
        PendingIntent openPending = PendingIntent.getActivity(context, 0, openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_root, openPending);

        if (hasTask) {
            Intent toggleIntent = new Intent(context, OneThingWidget.class).setAction(ACTION_TOGGLE);
            PendingIntent togglePending = PendingIntent.getBroadcast(context, 1, toggleIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_toggle, togglePending);
        }
        return views;
    }
}

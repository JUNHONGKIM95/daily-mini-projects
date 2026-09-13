package com.junhong.onething;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

public final class TodayNotification {
    private static final String CHANNEL_ID = "one_thing_reminder";
    private static final int NOTIFICATION_ID = 100;

    private TodayNotification() {}

    public static void sync(Context context) {
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        TodayStore.Task task = TodayStore.read(context);

        if (task.text.isEmpty() || task.completed) {
            manager.cancel(NOTIFICATION_ID);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setDescription(context.getString(R.string.notification_channel_description));
        manager.createNotificationChannel(channel);

        Intent openIntent = new Intent(context, MainActivity.class);
        PendingIntent openPending = PendingIntent.getActivity(
                context,
                20_001,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Intent completeIntent = new Intent(context, OneThingWidget.class)
                .setAction(OneThingWidget.ACTION_TOGGLE);
        PendingIntent completePending = PendingIntent.getBroadcast(
                context,
                20_002,
                completeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification = new Notification.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(task.text)
                .setStyle(new Notification.BigTextStyle().bigText(task.text))
                .setContentIntent(openPending)
                .addAction(R.drawable.notification_icon, context.getString(R.string.notification_complete), completePending)
                .setCategory(Notification.CATEGORY_REMINDER)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .build();
        manager.notify(NOTIFICATION_ID, notification);
    }
}

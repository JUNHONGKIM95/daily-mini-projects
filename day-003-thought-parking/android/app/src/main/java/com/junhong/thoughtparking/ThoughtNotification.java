package com.junhong.thoughtparking;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.List;

public final class ThoughtNotification {
    private static final String CHANNEL_ID = "parked_thoughts";
    private static final int NOTIFICATION_ID = 300;

    private ThoughtNotification() {}

    public static void sync(Context context) {
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        List<ThoughtStore.Thought> thoughts = ThoughtStore.read(context);
        if (thoughts.isEmpty()) {
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

        Intent openIntent = new Intent(context, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent openPending = PendingIntent.getActivity(
                context, 30_001, openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Intent resolveIntent = new Intent(context, ThoughtNotificationReceiver.class)
                .setAction(ThoughtNotificationReceiver.ACTION_RESOLVE_LATEST);
        PendingIntent resolvePending = PendingIntent.getBroadcast(
                context, 30_002, resolveIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        ThoughtStore.Thought latest = thoughts.get(0);
        String title = context.getResources().getQuantityString(
                R.plurals.notification_title, thoughts.size(), thoughts.size()
        );
        Notification notification = new Notification.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(latest.text)
                .setStyle(new Notification.BigTextStyle().bigText(latest.text))
                .setContentIntent(openPending)
                .addAction(R.drawable.notification_icon, context.getString(R.string.notification_resolve_latest), resolvePending)
                .setCategory(Notification.CATEGORY_REMINDER)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .build();
        manager.notify(NOTIFICATION_ID, notification);
    }
}

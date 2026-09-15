package com.junhong.thoughtparking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public final class ThoughtNotificationReceiver extends BroadcastReceiver {
    public static final String ACTION_RESOLVE_LATEST = "com.junhong.thoughtparking.RESOLVE_LATEST";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_RESOLVE_LATEST.equals(intent.getAction())) {
            ThoughtStore.removeLatest(context);
        }
        ThoughtNotification.sync(context);
    }
}

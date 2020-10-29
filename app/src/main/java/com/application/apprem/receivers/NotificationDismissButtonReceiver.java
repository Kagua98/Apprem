package com.application.apprem.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import java.util.Objects;

public class NotificationDismissButtonReceiver extends BroadcastReceiver {
    public static final String EXTRA_NOTIFICATION_ID = "EXTRA_NOTIFICATION_ID";

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        // if you want cancel notification
        int notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0);

        // if you want cancel notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Objects.requireNonNull(manager).cancel(notificationId);
    }
}

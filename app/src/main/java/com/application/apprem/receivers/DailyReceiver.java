package com.application.apprem.receivers;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.application.apprem.utils.NotificationUtil;
import com.application.apprem.utils.PreferenceUtil;

import androidx.annotation.NonNull;


public class DailyReceiver extends BroadcastReceiver {

    public static final int DailyReceiverID = 10000;

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                // Set the alarm here.
                if (PreferenceUtil.isAlarmOn(context)) {
                    int[] times = PreferenceUtil.getAlarmTime(context);
                    PreferenceUtil.setRepeatingAlarm(context, DailyReceiver.class, times[0], times[1], times[2], DailyReceiverID, AlarmManager.INTERVAL_DAY);
                } else
                    PreferenceUtil.cancelAlarm(context, DailyReceiver.class, DailyReceiverID);
                NotificationUtil.sendNotificationSummary(context, false);
                return;
            }
        }

        if (!PreferenceUtil.isAlarmOn(context)) {
            PreferenceUtil.cancelAlarm(context, DailyReceiver.class, DailyReceiverID);
        } else {
            NotificationUtil.sendNotificationSummary(context, true);
        }
    }

}
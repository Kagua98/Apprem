package com.application.apprem.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.application.apprem.profiles.ProfileManagement
import com.application.apprem.utils.DbHelper
import com.application.apprem.utils.NotificationUtil
import com.application.apprem.utils.PreferenceUtil
import java.util.*

class TurnOnReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != null) {
            if (intent.action.equals(Intent.ACTION_BOOT_COMPLETED, ignoreCase = true)) {
                setDoNotDisturbReceivers(context)
                NotificationUtil.sendNotificationCurrentLesson(context, false)
                return
            }
        }

        setDoNotDisturbReceivers(context)
        setDoNotDisturb(context, true)
    }

    companion object {
        const val TurnOn_ID = 30000
    }
}

class TurnOffReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        setDoNotDisturbReceivers(context, true)
        if (PreferenceUtil.isDoNotDisturbTurnOff(context))
            setDoNotDisturb(context, false)
        if (PreferenceUtil.isNotificationAtEnd(context))
            NotificationUtil.sendNotificationCurrentLesson(context, false)
    }

    companion object {
        const val TurnOff_ID = 60000
    }
}


fun setDoNotDisturb(context: Context, on: Boolean) {
    if (!PreferenceUtil.isAutomaticDoNotDisturb(context))
        return

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        // Check if the notification policy access has been granted for the app.
        if (notificationManager.isNotificationPolicyAccessGranted) {
            val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.setInterruptionFilter(if (on) NotificationManager.INTERRUPTION_FILTER_NONE else NotificationManager.INTERRUPTION_FILTER_ALL)
        }
    }
}

fun setDoNotDisturbReceivers(context: Context, onlyReceivers: Boolean = false) {
    ProfileManagement.initProfiles(context)
    if (!ProfileManagement.isPreferredProfile())
        return
    setSubjectReminder(context)
    Thread {
        val dbHelper = DbHelper(context, ProfileManagement.getPreferredProfilePosition())
        val calendar = Calendar.getInstance()
        val currentDay = NotificationUtil.getCurrentDay(calendar.get(Calendar.DAY_OF_WEEK))
        val weeks = dbHelper.getWeek(currentDay)

        var lastCalendar = Calendar.getInstance()
        lastCalendar.set(Calendar.HOUR_OF_DAY, 23)
        lastCalendar.set(Calendar.MINUTE, 59)
        var on: Boolean? = null

        for (week in weeks) {
            val weekCalendarStart = Calendar.getInstance()
            val startHour = Integer.parseInt(week.fromTime.substring(0, week.fromTime.indexOf(":")))
            weekCalendarStart.set(Calendar.HOUR_OF_DAY, startHour)
            val startMinute = Integer.parseInt(week.fromTime.substring(week.fromTime.indexOf(":") + 1))
            weekCalendarStart.set(Calendar.MINUTE, startMinute)

            if (((startHour == calendar.get(Calendar.HOUR_OF_DAY) && startMinute > calendar.get(Calendar.MINUTE)) || startHour > calendar.get(Calendar.HOUR_OF_DAY)) && ((startHour == lastCalendar.get(Calendar.HOUR_OF_DAY) && startMinute < lastCalendar.get(Calendar.MINUTE)) || startHour < lastCalendar.get(Calendar.HOUR_OF_DAY))) {
                lastCalendar = weekCalendarStart
                on = true
            }

            val weekCalendarEnd = Calendar.getInstance()
            val endHour = Integer.parseInt(week.toTime.substring(0, week.toTime.indexOf(":")))
            weekCalendarEnd.set(Calendar.HOUR_OF_DAY, endHour)
            val endMinute = Integer.parseInt(week.toTime.substring(week.toTime.indexOf(":") + 1))
            weekCalendarEnd.set(Calendar.MINUTE, endMinute)

            if (((endHour == calendar.get(Calendar.HOUR_OF_DAY) && endMinute > calendar.get(Calendar.MINUTE)) || endHour > calendar.get(Calendar.HOUR_OF_DAY)) && ((endHour == lastCalendar.get(Calendar.HOUR_OF_DAY) && endMinute < lastCalendar.get(Calendar.MINUTE)) || endHour < lastCalendar.get(Calendar.HOUR_OF_DAY))) {
                lastCalendar = weekCalendarEnd
                on = false
            }

            if (((startHour == calendar.get(Calendar.HOUR_OF_DAY) && startMinute < calendar.get(Calendar.MINUTE)) || startHour < calendar.get(Calendar.HOUR_OF_DAY)) && ((endHour == calendar.get(Calendar.HOUR_OF_DAY) && endMinute > calendar.get(Calendar.MINUTE)) || endHour > calendar.get(Calendar.HOUR_OF_DAY)) && !onlyReceivers) {
                //Just in lesson
                setDoNotDisturb(context, true)
            }
        }

        if (on != null) {
            if (on) {
                PreferenceUtil.setOneTimeAlarm(context, TurnOnReceiver::class.java, lastCalendar.get(Calendar.HOUR_OF_DAY), lastCalendar.get(Calendar.MINUTE), 0, TurnOnReceiver.TurnOn_ID)
            } else {
                PreferenceUtil.setOneTimeAlarm(context, TurnOffReceiver::class.java, lastCalendar.get(Calendar.HOUR_OF_DAY), lastCalendar.get(Calendar.MINUTE), 0, TurnOffReceiver.TurnOff_ID)
            }
        }
    }.start()
}
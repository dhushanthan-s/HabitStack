package org.habitstack.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import org.habitstack.data.db.Habit

object ReminderScheduler {

    private const val ACTION_REMIND = "org.habitstack.REMIND"
    private const val EXTRA_HABIT_ID = "habit_id"
    private const val EXTRA_HABIT_NAME = "habit_name"

    fun schedule(context: Context, habit: Habit) {
        val (hour, minute) = habit.reminderHour to habit.reminderMinute
        if (hour == null || minute == null) {
            cancel(context, habit.id)
            return
        }
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = ACTION_REMIND
            putExtra(EXTRA_HABIT_ID, habit.id)
            putExtra(EXTRA_HABIT_NAME, habit.name)
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val pending = PendingIntent.getBroadcast(context, habit.id.toInt(), intent, flags)

        val triggerTime = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, hour)
            set(java.util.Calendar.MINUTE, minute)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) add(java.util.Calendar.DAY_OF_YEAR, 1)
        }.timeInMillis

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            AlarmManager.INTERVAL_DAY,
            pending
        )
    }

    fun cancel(context: Context, habitId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = ACTION_REMIND
            putExtra(EXTRA_HABIT_ID, habitId)
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or
            if (Build.VERSION_SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val pending = PendingIntent.getBroadcast(context, habitId.toInt(), intent, flags)
        alarmManager.cancel(pending)
    }
}

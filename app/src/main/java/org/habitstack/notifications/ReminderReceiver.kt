package org.habitstack.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import org.habitstack.HabitStackApp
import org.habitstack.R

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != "org.habitstack.REMIND") return
        val habitName = intent.getStringExtra("habit_name") ?: "Habit"
        val habitId = intent.getLongExtra("habit_id", -1L)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context, HabitStackApp.CHANNEL_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_menu_my_calendar)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.reminder_content, habitName))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notifId = habitId.toInt().let { if (it < 0) 0 else it }
        notificationManager.notify(notifId, builder.build())
    }
}

package org.habitstack

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import org.habitstack.data.HabitRepository
import org.habitstack.data.db.AppDatabase

class HabitStackApp : Application() {

    val database by lazy { AppDatabase.getInstance(this) }
    val habitRepository by lazy {
        HabitRepository(
            habitDao = database.habitDao(),
            habitCompletionDao = database.habitCompletionDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_REMINDERS,
                getString(R.string.channel_reminders_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.channel_reminders_desc)
                setShowBadge(true)
            }
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_REMINDERS = "habit_reminders"
    }
}

package org.habitstack.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val colorArgb: Int,
    val iconName: String = "check", // Material icon name
    val reminderHour: Int? = null,  // 0-23, null = no reminder
    val reminderMinute: Int? = null,
    val archived: Boolean = false,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

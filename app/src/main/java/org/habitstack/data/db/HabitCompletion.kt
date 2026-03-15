package org.habitstack.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "habit_completions",
    primaryKeys = ["habitId", "dayStartMillis"],
    foreignKeys = [
        ForeignKey(
            entity = Habit::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("habitId"), Index("dayStartMillis")]
)
data class HabitCompletion(
    val habitId: Long,
    /** Start of day in UTC millis (e.g. midnight local or UTC depending on your rule) */
    val dayStartMillis: Long,
    val completedAt: Long = System.currentTimeMillis()
)

package org.habitstack.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.habitstack.HabitStackApp
import org.habitstack.data.db.Habit
import org.habitstack.notifications.ReminderScheduler

class AddEditHabitViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as HabitStackApp
    private val repo = app.habitRepository

    private val _habit = MutableStateFlow<Habit?>(null)
    val habit: StateFlow<Habit?> = _habit.asStateFlow()

    fun loadHabit(id: Long) {
        viewModelScope.launch {
            _habit.value = repo.getHabitById(id)
        }
    }

    suspend fun saveHabit(
        id: Long?,
        name: String,
        description: String,
        colorArgb: Int,
        reminderHour: Int?,
        reminderMinute: Int?
    ) {
        val existing = id?.let { repo.getHabitById(it) }
        val toSave = if (existing != null) {
            existing.copy(
                name = name,
                description = description,
                colorArgb = colorArgb,
                reminderHour = reminderHour,
                reminderMinute = reminderMinute
            )
        } else {
            val maxOrder = repo.habits.first().maxOfOrNull { it.sortOrder } ?: -1
            Habit(
                name = name,
                description = description,
                colorArgb = colorArgb,
                reminderHour = reminderHour,
                reminderMinute = reminderMinute,
                sortOrder = maxOrder + 1
            )
        }
        val savedHabit = if (existing != null) {
            repo.updateHabit(toSave)
            toSave
        } else {
            val newId = repo.insertHabit(toSave)
            toSave.copy(id = newId)
        }
        ReminderScheduler.schedule(app, savedHabit)
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            ReminderScheduler.cancel(app, habit.id)
            repo.deleteHabit(habit)
        }
    }
}

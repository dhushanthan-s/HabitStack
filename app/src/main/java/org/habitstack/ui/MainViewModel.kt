package org.habitstack.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.habitstack.HabitStackApp
import org.habitstack.data.db.Habit

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as HabitStackApp
    private val repo = app.habitRepository

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    private val _completionDays = MutableStateFlow<Map<Long, List<Long>>>(emptyMap())
    val completionDays: StateFlow<Map<Long, List<Long>>> = _completionDays.asStateFlow()

    init {
        repo.habits.onEach { list ->
            _habits.value = list
            list.map { it.id }.forEach { id ->
                viewModelScope.launch {
                    repo.getCompletionDays(id).onEach { days ->
                        _completionDays.value = _completionDays.value + (id to days)
                    }.launchIn(viewModelScope)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun toggleToday(habitId: Long) {
        viewModelScope.launch {
            val today = repo.getTodayStartMillis()
            repo.toggleCompletion(habitId, today)
        }
    }

    fun getTodayStartMillis(): Long = repo.getTodayStartMillis()
    fun getDayStartMillis(time: Long): Long = repo.getDayStartMillis(time)
}

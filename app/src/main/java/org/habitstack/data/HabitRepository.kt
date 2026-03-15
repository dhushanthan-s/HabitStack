package org.habitstack.data

import kotlinx.coroutines.flow.Flow
import org.habitstack.data.db.Habit
import org.habitstack.data.db.HabitCompletion
import org.habitstack.data.db.HabitCompletionDao
import org.habitstack.data.db.HabitDao
import java.util.Calendar
import java.util.TimeZone

class HabitRepository(
    private val habitDao: HabitDao,
    private val completionDao: HabitCompletionDao
) {

    val habits: Flow<List<Habit>> = habitDao.getAllHabits()

    suspend fun getHabitById(id: Long): Habit? = habitDao.getHabitById(id)

    fun getCompletionDays(habitId: Long): Flow<List<Long>> = completionDao.getCompletionDaysForHabit(habitId)

    suspend fun insertHabit(habit: Habit): Long = habitDao.insert(habit)

    suspend fun updateHabit(habit: Habit) = habitDao.update(habit)

    suspend fun deleteHabit(habit: Habit) = habitDao.setArchived(habit.id, true)

    suspend fun toggleCompletion(habitId: Long, dayStartMillis: Long) {
        val existing = completionDao.get(habitId, dayStartMillis)
        if (existing != null) completionDao.delete(habitId, dayStartMillis)
        else completionDao.insert(HabitCompletion(habitId = habitId, dayStartMillis = dayStartMillis))
    }

    suspend fun setCompleted(habitId: Long, dayStartMillis: Long, completed: Boolean) {
        if (completed) completionDao.insert(HabitCompletion(habitId = habitId, dayStartMillis = dayStartMillis))
        else completionDao.delete(habitId, dayStartMillis)
    }

    fun getTodayStartMillis(): Long = getDayStartMillis(System.currentTimeMillis())

    /** Returns start of day (midnight) in device's local timezone for the given time. */
    fun getDayStartMillis(timeMillis: Long): Long {
        val cal = Calendar.getInstance(TimeZone.getDefault())
        cal.timeInMillis = timeMillis
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}

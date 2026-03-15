package org.habitstack.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HabitCompletionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(completion: HabitCompletion)

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId AND dayStartMillis = :dayStartMillis")
    suspend fun delete(habitId: Long, dayStartMillis: Long)

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId AND dayStartMillis = :dayStartMillis LIMIT 1")
    suspend fun get(habitId: Long, dayStartMillis: Long): HabitCompletion?

    @Query("SELECT dayStartMillis FROM habit_completions WHERE habitId = :habitId ORDER BY dayStartMillis ASC")
    fun getCompletionDaysForHabit(habitId: Long): Flow<List<Long>>

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId")
    fun getAllCompletionsForHabit(habitId: Long): Flow<List<HabitCompletion>>
}

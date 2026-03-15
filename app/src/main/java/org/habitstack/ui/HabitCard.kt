package org.habitstack.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.habitstack.data.db.Habit

private const val GRID_WEEKS = 12
private const val GRID_DAYS_PER_WEEK = 7

@Composable
fun HabitCard(
    habit: Habit,
    completedDays: List<Long>,
    onTap: () -> Unit,
    onToggleToday: () -> Unit,
    getTodayStart: () -> Long,
    getDayStart: (Long) -> Long,
    modifier: Modifier = Modifier
) {
    val habitColor = Color(habit.colorArgb)
    val todayStart = getTodayStart()
    val completedSet = completedDays.toSet()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onTap)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(habitColor)
            )
            Text(
                text = habit.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
            )
        }

        // HabitKit-style grid: 7 columns (Sun–Sat), rows = weeks (oldest at top left)
        val totalDays = GRID_WEEKS * GRID_DAYS_PER_WEEK
        val dayStarts = (0 until totalDays).map { offset ->
            val cal = java.util.Calendar.getInstance()
            cal.timeInMillis = todayStart
            cal.add(java.util.Calendar.DAY_OF_YEAR, -totalDays + 1 + offset)
            getDayStart(cal.timeInMillis)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            for (col in 0 until GRID_DAYS_PER_WEEK) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    for (row in 0 until GRID_WEEKS) {
                        val index = row * GRID_DAYS_PER_WEEK + col
                        if (index < dayStarts.size) {
                            val dayStart = dayStarts[index]
                            val isCompleted = completedSet.contains(dayStart)
                            val isToday = dayStart == todayStart
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        when {
                                            isCompleted -> habitColor
                                            isToday -> habitColor.copy(alpha = 0.3f)
                                            else -> MaterialTheme.colorScheme.surface
                                    }
                                    )
                                    .then(
                                        if (isToday) Modifier.clickable(onClick = onToggleToday)
                                        else Modifier
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

package org.habitstack.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.habitstack.R
import org.habitstack.data.db.Habit

@Composable
fun DashboardScreen(
    habits: List<Habit>,
    completionDays: Map<Long, List<Long>>,
    onHabitClick: (Long) -> Unit,
    onToggleToday: (Long) -> Unit,
    getTodayStart: () -> Long,
    getDayStart: (Long) -> Long,
    modifier: Modifier = Modifier
) {
    if (habits.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.no_habits),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.no_habits_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(habits, key = { it.id }) { habit ->
            HabitCard(
                habit = habit,
                completedDays = completionDays[habit.id] ?: emptyList(),
                onTap = { onHabitClick(habit.id) },
                onToggleToday = { onToggleToday(habit.id) },
                getTodayStart = getTodayStart,
                getDayStart = getDayStart
            )
        }
        // Extra padding at bottom for FAB
        item {
            Box(modifier = Modifier.padding(bottom = 80.dp)) {}
        }
    }
}

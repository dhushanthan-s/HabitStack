package org.habitstack.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import org.habitstack.R

@Composable
fun HabitStackApp(
    mainViewModel: MainViewModel = viewModel()
) {
    val navController = rememberNavController()
    val habits by mainViewModel.habits.collectAsState(initial = emptyList())
    val completionDays by mainViewModel.completionDays.collectAsState(initial = emptyMap())
    var showAdd by remember { mutableStateOf(false) }
    var editHabitId by remember { mutableLongStateOf(0L) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAdd = true },
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.habit_add))
            }
        }
    ) { padding ->
        if (showAdd) {
            AddEditHabitScreen(
                habitId = null,
                onDismiss = { showAdd = false },
                onSaved = { showAdd = false },
                modifier = Modifier.padding(padding)
            )
        } else if (editHabitId != 0L) {
            AddEditHabitScreen(
                habitId = editHabitId,
                onDismiss = { editHabitId = 0L },
                onSaved = { editHabitId = 0L },
                modifier = Modifier.padding(padding)
            )
        } else {
            DashboardScreen(
                habits = habits,
                completionDays = completionDays,
                onHabitClick = { editHabitId = it },
                onToggleToday = mainViewModel::toggleToday,
                getTodayStart = mainViewModel::getTodayStartMillis,
                getDayStart = mainViewModel::getDayStartMillis,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

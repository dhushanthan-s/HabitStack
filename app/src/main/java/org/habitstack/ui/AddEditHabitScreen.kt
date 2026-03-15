package org.habitstack.ui

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import org.habitstack.R
import org.habitstack.data.db.Habit
import org.habitstack.ui.theme.HabitColors

@Composable
fun AddEditHabitScreen(
    habitId: Long?,
    onDismiss: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AddEditHabitViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val habit by viewModel.habit.collectAsState()
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf(habit?.name ?: "") }
    var description by remember { mutableStateOf(habit?.description ?: "") }
    var colorArgb by remember(habit) { mutableIntStateOf(habit?.colorArgb ?: HabitColors.first().toArgb()) }
    var reminderHour by remember(habit) { mutableIntStateOf(habit?.reminderHour ?: 9) }
    var reminderMinute by remember(habit) { mutableIntStateOf(habit?.reminderMinute ?: 0) }
    var reminderEnabled by remember(habit) { mutableStateOf(habit?.reminderHour != null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(habitId) {
        if (habitId != null) viewModel.loadHabit(habitId)
    }
    LaunchedEffect(habit) {
        habit?.let { h ->
            name = h.name
            description = h.description
            colorArgb = h.colorArgb
            reminderHour = h.reminderHour ?: 9
            reminderMinute = h.reminderMinute ?: 0
            reminderEnabled = h.reminderHour != null
        }
    }

    if (showDeleteConfirm && habit != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(stringResource(R.string.delete)) },
            text = { Text("Delete \"${habit!!.name}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteHabit(habit!!)
                        showDeleteConfirm = false
                        onSaved()
                    }
                ) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(if (habitId == null) "New habit" else "Edit habit") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (habit != null) {
                        TextButton(onClick = { showDeleteConfirm = true }) {
                            Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.habit_name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.habit_description)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                minLines = 2
            )

            Text(
                "Color",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(top = 20.dp, bottom = 8.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HabitColors.forEach { color ->
                    val selected = color.toArgb() == colorArgb
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(color, CircleShape)
                            .then(
                                if (selected) Modifier
                                    .padding(2.dp)
                                    .background(MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                    .padding(2.dp)
                                    .background(color, CircleShape)
                                else Modifier
                            )
                            .clickable { colorArgb = color.toArgb() }
                    )
                }
            }

            Text(
                stringResource(R.string.reminder),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(top = 20.dp, bottom = 8.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            val context = LocalContext.current
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    if (reminderEnabled) "%02d:%02d".format(reminderHour, reminderMinute)
                    else "Off",
                    style = MaterialTheme.typography.bodyLarge
                )
                Row {
                    TextButton(onClick = {
                        if (reminderEnabled) {
                            TimePickerDialog(
                                context,
                                { _, h, m ->
                                    reminderHour = h
                                    reminderMinute = m
                                },
                                reminderHour,
                                reminderMinute,
                                true
                            ).show()
                        }
                    }) { Text(if (reminderEnabled) "Change" else "Set") }
                    if (reminderEnabled) {
                        TextButton(onClick = { reminderEnabled = false }) { Text("Off") }
                    } else {
                        TextButton(onClick = {
                            reminderEnabled = true
                            TimePickerDialog(
                                context,
                                { _, h, m ->
                                    reminderHour = h
                                    reminderMinute = m
                                },
                                9, 0, true
                            ).show()
                        }) { Text("Set time") }
                    }
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        viewModel.saveHabit(
                            id = habitId,
                            name = name,
                            description = description,
                            colorArgb = colorArgb,
                            reminderHour = if (reminderEnabled) reminderHour else null,
                            reminderMinute = if (reminderEnabled) reminderMinute else null
                        )
                        onSaved()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}

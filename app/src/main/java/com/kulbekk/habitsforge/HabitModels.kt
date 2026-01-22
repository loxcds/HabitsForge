package com.kulbekk.habitsforge

import android.app.TimePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.util.Calendar

/**
 * Data model for a Good Habit.
 */
data class Habit(
    val title: String,
    val description: String,
    val imageUri: Uri?,
    val schedule: List<String>,
    val startTime: String,
    val endTime: String
)

/**
 * Dialog for creating a new Good Habit.
 * @param selectedDay The day automatically chosen from the calendar.
 */
@Composable
fun CreateHabitDialog(
    selectedDay: String,
    onDismiss: () -> Unit,
    onSave: (Habit) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var startTime by remember { mutableStateOf("09:00") }
    var endTime by remember { mutableStateOf("10:00") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val startTimePicker = TimePickerDialog(
        context,
        { _, hour, minute ->
            startTime = String.format("%02d:%02d", hour, minute)
        },
        9, 0, true
    )

    val endTimePicker = TimePickerDialog(
        context,
        { _, hour, minute ->
            endTime = String.format("%02d:%02d", hour, minute)
        },
        10, 0, true
    )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text("Новая привычка", style = MaterialTheme.typography.headlineSmall)
                }
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Название") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Описание") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Button(onClick = { launcher.launch("image/*") }) {
                        Text(if (imageUri == null) "Прикрепить изображение" else "Изображение прикреплено")
                    }
                    if (imageUri != null) {
                        Text(
                            text = "Selected: ${imageUri?.lastPathSegment}",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                item {
                    Text("День недели: $selectedDay", style = MaterialTheme.typography.bodyLarge)
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Начало", style = MaterialTheme.typography.labelMedium)
                            OutlinedButton(onClick = { startTimePicker.show() }) {
                                Text(startTime)
                            }
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Конец", style = MaterialTheme.typography.labelMedium)
                            OutlinedButton(onClick = { endTimePicker.show() }) {
                                Text(endTime)
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) { Text("Cancel") }
                        TextButton(
                            onClick = { 
                                onSave(Habit(title, description, imageUri, listOf(selectedDay), startTime, endTime)) 
                            },
                            enabled = title.isNotBlank()
                        ) { Text("Save") }
                    }
                }
            }
        }
    }
}

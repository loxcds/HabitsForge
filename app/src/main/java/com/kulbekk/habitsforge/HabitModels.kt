package com.kulbekk.habitsforge

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Calendar

/**
 * Data model for a Good Habit.
 */
@Serializable
data class Habit(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val imageUriString: String?,
    val selectedDates: List<LocalDate>
) {
    val imageUri: Uri? get() = imageUriString?.let { Uri.parse(it) }
}

/**
 * Helper class to persist habits.
 */
object HabitRepository {
    private const val PREFS_NAME = "habits_prefs"
    private const val KEY_HABITS = "habits_list"

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    fun saveHabits(context: Context, habits: List<Habit>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        try {
            val jsonString = json.encodeToString(habits)
            prefs.edit().putString(KEY_HABITS, jsonString).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadHabits(context: Context): List<Habit> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonString = prefs.getString(KEY_HABITS, null)
        return if (jsonString != null) {
            try {
                json.decodeFromString<List<Habit>>(jsonString)
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        } else {
            emptyList()
        }
    }
}

/**
 * Helper function to get Russian month name.
 */
fun getRussianMonthName(month: Month): String {
    return when (month) {
        Month.JANUARY -> "Январь"
        Month.FEBRUARY -> "Февраль"
        Month.MARCH -> "Март"
        Month.APRIL -> "Апрель"
        Month.MAY -> "Май"
        Month.JUNE -> "Июнь"
        Month.JULY -> "Июль"
        Month.AUGUST -> "Август"
        Month.SEPTEMBER -> "Сентябрь"
        Month.OCTOBER -> "Октябрь"
        Month.NOVEMBER -> "Ноябрь"
        Month.DECEMBER -> "Декабрь"
        else -> month.name
    }
}

/**
 * Shared Month Title component to ensure consistency across the app.
 */
@Composable
fun CalendarMonthTitle(month: Month, year: Int, modifier: Modifier = Modifier) {
    Text(
        text = "${getRussianMonthName(month)} $year",
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

/**
 * Composable for displaying a single habit item in a list.
 */
@Composable
fun HabitItem(habit: Habit, onDelete: () -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { isExpanded = !isExpanded },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = habit.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (habit.description.isNotBlank()) {
                        Text(text = habit.description, style = MaterialTheme.typography.bodySmall)
                    }
                }

                if (isExpanded) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = Color.Red)
                    }
                }
            }

            if (isExpanded && habit.imageUriString != null) {
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = habit.imageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

/**
 * Dialog for creating a new Good Habit.
 */
@Composable
fun CreateHabitDialog(
    initialSelectedDate: LocalDate? = null,
    onDismiss: () -> Unit,
    onSave: (Habit) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUriString by remember { mutableStateOf<String?>(null) }
    val selectedDates = remember {
        mutableStateListOf<LocalDate>().apply {
            initialSelectedDate?.let { add(it) }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUriString = uri?.toString()
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Новая привычка", style = MaterialTheme.typography.headlineSmall)

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Название") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(onClick = { launcher.launch("image/*") }) {
                    Text(if (imageUriString == null) "Прикрепить изображение" else "Изображение прикреплено")
                }

                Text("Выберите даты", style = MaterialTheme.typography.titleMedium)

                MonthPicker(
                    onDateClick = { date ->
                        if (selectedDates.contains(date)) {
                            selectedDates.remove(date)
                        } else {
                            selectedDates.add(date)
                        }
                    },
                    selectedDates = selectedDates,
                    displayMonthDate = initialSelectedDate
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Отмена") }
                    TextButton(
                        onClick = {
                            onSave(Habit(title = title, description = description, imageUriString = imageUriString, selectedDates = selectedDates.toList()))
                        },
                        enabled = title.isNotBlank() && selectedDates.isNotEmpty()
                    ) { Text("Сохранить") }
                }
            }
        }
    }
}

@Composable
fun MonthPicker(
    onDateClick: (LocalDate) -> Unit,
    selectedDates: List<LocalDate>,
    displayMonthDate: LocalDate? = null
) {
    val baseDate = remember(displayMonthDate) {
        displayMonthDate ?: run {
            val calendar = Calendar.getInstance()
            LocalDate(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        }
    }

    val year = baseDate.year
    val month = baseDate.month
    val daysInMonth = remember(year, month) {
        val start = LocalDate(year, month, 1)
        val list = mutableListOf<LocalDate>()
        var current = start
        while (current.month == month) {
            list.add(current)
            try {
                val next = current.plus(1, DateTimeUnit.DAY)
                if (next.month != month) break
                current = next
            } catch (e: Exception) {
                break
            }
        }
        list
    }

    val firstDayOfMonth = LocalDate(year, month, 1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.isoDayNumber // 1 (Mon) to 7 (Sun).
    val emptySlots = firstDayOfWeek - 1

    Column {
        CalendarMonthTitle(month = month, year = year)

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            val dayLabels = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
            dayLabels.forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(200.dp).fillMaxWidth()
        ) {
            items(emptySlots) {
                Box(modifier = Modifier.aspectRatio(1f))
            }
            items(daysInMonth) { date ->
                val isSelected = selectedDates.contains(date)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { onDateClick(date) }
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

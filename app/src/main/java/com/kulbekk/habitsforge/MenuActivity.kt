package com.kulbekk.habitsforge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*
import java.util.Calendar

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize().statusBarsPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MenuCalendarScreen()
                }
            }
        }
    }
}

/**
 * Data class for representing a day in the menu calendar.
 */
data class MenuCalendarDay(
    val date: LocalDate,
    val dayOfMonth: String,
    val dayOfWeek: String,
    val isSelected: Boolean = false
)

/**
 * Composable for displaying an individual calendar day item.
 */
@Composable
fun MenuDayItem(
    day: MenuCalendarDay,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 4.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Text(
            text = day.dayOfWeek,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (day.isSelected) MaterialTheme.colorScheme.primary else Color.Gray
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (day.isSelected) MaterialTheme.colorScheme.primary else Color.LightGray)
        ) {
            Text(
                text = day.dayOfMonth,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (day.isSelected) Color.White else Color.Black
            )
        }
    }
}

/**
 * Horizontal row of days with auto-scroll to the selected index.
 */
@Composable
fun MenuScrollableCalendarRow(
    days: List<MenuCalendarDay>,
    onDayClick: (MenuCalendarDay) -> Unit
) {
    val listState = rememberLazyListState()
    val selectedIndex = days.indexOfFirst { it.isSelected }

    LaunchedEffect(selectedIndex) {
        if (selectedIndex != -1) {
            listState.animateScrollToItem(index = selectedIndex)
        }
    }

    LazyRow(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(days, key = { it.date.toString() }) { day ->
            MenuDayItem(
                day = day,
                onClick = { onDayClick(day) }
            )
        }
    }
}

/**
 * Main calendar screen Composable.
 */
@Composable
fun MenuCalendarScreen() {
    val context = LocalContext.current
    val today = remember {
        val calendar = Calendar.getInstance()
        LocalDate(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    val habits = remember {
        mutableStateListOf<Habit>().apply {
            addAll(HabitRepository.loadHabits(context))
        }
    }

    val daysList = remember(selectedDate) {
        generateMenuDays(
            startDate = today.minus(30, DateTimeUnit.DAY),
            count = 120,
            selectedDate = selectedDate
        )
    }

    val displayDate = selectedDate ?: today

    // Unified LazyColumn for the entire screen to ensure scrollability
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            CalendarMonthTitle(month = displayDate.month, year = displayDate.year)
        }

        item {
            MenuScrollableCalendarRow(
                days = daysList,
                onDayClick = { clickedDay ->
                    selectedDate = if (selectedDate == clickedDay.date) null else clickedDay.date
                }
            )
        }

        if (selectedDate != null) {
            val habitsForDay = habits.filter { it.selectedDates.contains(selectedDate!!) }

            if (habitsForDay.isNotEmpty()) {
                items(habitsForDay, key = { it.id }) { habit ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        HabitItem(
                            habit = habit,
                            onDelete = {
                                habits.remove(habit)
                                HabitRepository.saveHabits(context, habits)
                            }
                        )
                    }
                }
            } else {
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Нет привычек на этот день",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text("Добавить новую привычку")
                }
                Spacer(modifier = Modifier.height(48.dp)) // Extra space at bottom
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Выберите день, чтобы увидеть привычки",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }
    }

    if (showDialog) {
        CreateHabitDialog(
            initialSelectedDate = selectedDate,
            onDismiss = { showDialog = false },
            onSave = { habit ->
                habits.add(habit)
                HabitRepository.saveHabits(context, habits)
                showDialog = false
                selectedDate = null
            }
        )
    }
}

/**
 * Generates a list of days
 */
fun generateMenuDays(startDate: LocalDate, count: Int, selectedDate: LocalDate?): List<MenuCalendarDay> {
    val dayNames = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")

    return List(count) { i ->
        val date = startDate.plus(i, DateTimeUnit.DAY)
        val dayOfWeekIndex = date.dayOfWeek.isoDayNumber % 7

        MenuCalendarDay(
            date = date,
            dayOfMonth = date.dayOfMonth.toString(),
            dayOfWeek = dayNames[dayOfWeekIndex],
            isSelected = date == selectedDate
        )
    }
}

package com.kulbekk.habitsforge

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.datetime.*


class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    // Data class для представления дня
    data class CalendarDay(
        val date: LocalDate,
        val dayOfMonth: String,
        val dayOfWeek: String,
        val isSelected: Boolean = false
    )

    /**
     * Composable-функция для отображения отдельного дня календаря.
     */
    @Composable
    fun DayItem(day: CalendarDay, onClick: () -> Unit) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 4.dp) // Небольшой горизонтальный отступ между днями
        ) {
            // День недели (над кругом)
            Text(
                text = day.dayOfWeek,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Gray // Цвет дня недели
            )

            Spacer(modifier = Modifier.height(4.dp)) // Отступ между днем недели и числом

            // Число в круге
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp) // Размер круга
                    .clip(CircleShape)
                    .background(if (day.isSelected) Color.Blue else Color.LightGray) // Цвет фона: синий для выбранного, светло-серый для остальных
            ) {
                Text(
                    text = day.dayOfMonth,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (day.isSelected) Color.White else Color.Black // Цвет текста
                )
            }
        }
    }

    /**
     * Composable-функция для отображения строки недели.
     */
    @Composable
    fun WeekRow(days: List<CalendarDay>) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp), // Отступ 10px сверху от самого верха
            horizontalArrangement = Arrangement.SpaceAround // Равномерное распределение элементов
        ) {
            days.forEach { day ->
                DayItem(day = day)
            }
        }
    }

@Composable
fun ScrollableCalendarRow(
    days: List<CalendarDay>,
    onDayClick: (CalendarDay) -> Unit
) {
    val listState = rememberLazyListState()
    val selectedIndex = days.indexOfFirst { it.isSelected }

    // Программная прокрутка к выбранному дню (например, сегодня) при первом запуске
    LaunchedEffect(selectedIndex) {
        if (selectedIndex != -1) {
            listState.animateScrollToItem(index = selectedIndex)
        }
    }

    LazyRow(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp), // Отступ сверху
        contentPadding = PaddingValues(horizontal = 8.dp), // Горизонтальные отступы по краям
        horizontalArrangement = Arrangement.spacedBy(4.dp) // Отступ между элементами
    ) {
        items(days, key = { it.date.toString() }) { day ->
            DayItem(
                day = day,
                onClick = { onDayClick(day) } 
            )
        }
    }
}

// Пример использования:

@Composable
fun MyCalendarScreen() {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    var selectedDate by remember { mutableStateOf(today) }

    // Генерируем дни: например, 30 дней назад и 90 дней вперед
    val daysList = remember(selectedDate) {
        generateDays(
            startDate = today.minus(30, DateTimeUnit.DAY),
            count = 120, // Общее количество дней для отображения
            selectedDate = selectedDate
        )
    }

    val handleDayClick: (CalendarDay) -> Unit = { clickedDay ->
        selectedDate = clickedDay.date // Обновляем выбранную дату
    }

    Column {
        ScrollableCalendarRow(
            days = daysList,
            onDayClick = handleDayClick
        )
    }
}

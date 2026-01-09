import kotlinx.datetime.*
import java.util.Locale
import java.time.format.TextStyle

fun generateDays(startDate: LocalDate, count: Int, selectedDate: LocalDate): List<CalendarDay> {
    return (0 until count).map { i ->
        val date = startDate.plus(i, DateTimeUnit.DAY)
        CalendarDay(
            date = date,
            isSelected = date == selectedDate
        )
    }
}
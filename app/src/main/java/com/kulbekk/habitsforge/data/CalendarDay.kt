import kotlinx.datetime.LocalDate

data class CalendarDay(
    val date: LocalDate,
    val isSelected: Boolean = false
) {
    val dayOfMonth: String
        get() = date.dayOfMonth.toString()
    val dayOfWeek: String
        get() = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru"))
}

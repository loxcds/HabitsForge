package com.kulbekk.habitsforge.data

import kotlinx.datetime.LocalDate

/**
 * Data class for representing a day in the calendar.
 */
data class CalendarDay(
    val date: LocalDate,
    val dayOfMonth: String,
    val dayOfWeek: String,
    val isSelected: Boolean = false
)

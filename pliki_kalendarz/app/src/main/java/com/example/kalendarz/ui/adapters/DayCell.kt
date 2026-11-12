package com.example.kalendarz.ui.adapters

import com.example.kalendarz.data.db.Event
import com.example.kalendarz.data.db.Holiday

data class DayCell(
    val dayNumber: Int,
    val dateString: String, // YYYY-MM-DD
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val holidays: List<Holiday> = emptyList(),
    val events: List<Event> = emptyList()
)

package com.example.kalendarz.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val date: String, // YYYY-MM-DD
    val time: String = "12:00",
    val themeKey: String = "gregorian",
    val location: String = ""
)

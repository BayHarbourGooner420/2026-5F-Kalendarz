package com.example.kalendarz.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "holidays")
data class Holiday(
    @PrimaryKey
    val id: String,
    val date: String, // YYYY-MM-DD
    val localName: String,
    val name: String,
    val countryCode: String,
    val flagEmoji: String = ""
)

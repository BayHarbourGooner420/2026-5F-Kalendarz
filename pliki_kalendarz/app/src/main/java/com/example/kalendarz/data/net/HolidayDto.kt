package com.example.kalendarz.data.net

data class HolidayDto(
    val date: String,
    val localName: String,
    val name: String,
    val countryCode: String,
    val fixed: Boolean = true,
    val global: Boolean = true
)

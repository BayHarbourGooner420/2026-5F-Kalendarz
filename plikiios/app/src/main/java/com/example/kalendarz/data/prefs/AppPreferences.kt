package com.example.kalendarz.data.prefs

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("kalendarz_prefs", Context.MODE_PRIVATE)

    var selectedCountries: Set<String>
        get() = prefs.getStringSet(KEY_COUNTRIES, DEFAULT_COUNTRIES) ?: DEFAULT_COUNTRIES
        set(value) = prefs.edit().putStringSet(KEY_COUNTRIES, value).apply()

    var enabledCalendars: Set<String>
        get() = prefs.getStringSet(KEY_CALENDARS, DEFAULT_CALENDARS) ?: DEFAULT_CALENDARS
        set(value) = prefs.edit().putStringSet(KEY_CALENDARS, value).apply()

    var activeTheme: String
        get() = prefs.getString(KEY_THEME, "gregorian") ?: "gregorian"
        set(value) = prefs.edit().putString(KEY_THEME, value).apply()

    companion object {
        private const val KEY_COUNTRIES = "selected_countries"
        private const val KEY_CALENDARS = "enabled_calendars"
        private const val KEY_THEME = "active_theme"

        val DEFAULT_COUNTRIES = setOf("US", "DE", "JP", "PL", "IN", "TR", "NG")
        val DEFAULT_CALENDARS = setOf("gregorian", "islamic", "jewish", "chinese", "hindu")
    }
}

package com.example.kalendarz.ui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kalendarz.R
import com.example.kalendarz.data.prefs.AppPreferences
import com.example.kalendarz.databinding.ActivitySettingsBinding
import com.example.kalendarz.databinding.ItemSettingsCalendarBinding
import com.example.kalendarz.databinding.ItemSettingsCountryBinding
import com.example.kalendarz.util.FlagUtils

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var appPrefs: AppPreferences

    private val countryCodes = listOf("US", "DE", "JP", "PL", "IN", "TR", "NG")

    private val calendarSystems = listOf(
        Triple("gregorian", "Kalendarz Gregoriański", R.drawable.ic_calendar),
        Triple("islamic", "Kalendarz Islamski (Hijri)", R.drawable.ic_islamic),
        Triple("jewish", "Kalendarz Żydowski", R.drawable.ic_jewish),
        Triple("chinese", "Kalendarz Chiński", R.drawable.ic_chinese),
        Triple("hindu", "Kalendarz Hinduski (Saka)", R.drawable.ic_hindu)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.rootSettings) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        appPrefs = AppPreferences(this)

        binding.btnBack.setOnClickListener { finish() }

        setupCountriesList()
        setupCalendarsList()
    }

    private fun setupCountriesList() {
        val selected = appPrefs.selectedCountries.toMutableSet()
        binding.containerCountries.removeAllViews()

        for (code in countryCodes) {
            val itemBinding = ItemSettingsCountryBinding.inflate(
                LayoutInflater.from(this),
                binding.containerCountries,
                false
            )

            itemBinding.tvCountryFlag.text = FlagUtils.getFlagEmoji(code)
            itemBinding.tvCountryName.text = FlagUtils.getCountryName(code)
            itemBinding.switchCountry.isChecked = selected.contains(code)

            itemBinding.switchCountry.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selected.add(code)
                } else {
                    selected.remove(code)
                }
                appPrefs.selectedCountries = selected
            }

            binding.containerCountries.addView(itemBinding.root)
        }
    }

    private fun setupCalendarsList() {
        val enabled = appPrefs.enabledCalendars.toMutableSet()
        binding.containerCalendars.removeAllViews()

        for (triple in calendarSystems) {
            val (key, name, iconRes) = triple
            val itemBinding = ItemSettingsCalendarBinding.inflate(
                LayoutInflater.from(this),
                binding.containerCalendars,
                false
            )

            itemBinding.imgCalendarIcon.setImageResource(iconRes)
            itemBinding.tvCalendarName.text = name
            itemBinding.cbCalendar.isChecked = enabled.contains(key)

            itemBinding.cbCalendar.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    enabled.add(key)
                } else {
                    enabled.remove(key)
                }
                appPrefs.enabledCalendars = enabled
                if (isChecked) {
                    appPrefs.activeTheme = key
                }
            }

            binding.containerCalendars.addView(itemBinding.root)
        }
    }
}

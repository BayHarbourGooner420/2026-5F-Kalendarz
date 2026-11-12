package com.example.kalendarz.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.kalendarz.data.db.AppDatabase
import com.example.kalendarz.data.db.Event
import com.example.kalendarz.databinding.ActivityAddEditEventBinding
import com.example.kalendarz.util.ThemeManager
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AddEditEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditEventBinding
    private lateinit var db: AppDatabase

    private var selectedThemeKey: String = "islamic" // Default matching Screenshot 2
    private var selectedDate: LocalDate = LocalDate.of(2026, 10, 15)
    private var selectedTime: LocalTime = LocalTime.of(19, 0)
    private var eventIdToEdit: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddEditEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.rootAddEvent) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = AppDatabase.getDatabase(this)
        eventIdToEdit = intent.getLongExtra("EVENT_ID", -1L)

        setupListeners()
        updateThemeVisuals(selectedThemeKey)

        if (eventIdToEdit != -1L) {
            loadEventToEdit(eventIdToEdit)
        } else {
            // Set default date/time matching Screenshot 2 ("Paździe 2026", "19:00", Title: "Uroczystość Eid")
            val dateExtra = intent.getStringExtra("PRESET_DATE")
            if (dateExtra != null) {
                try {
                    selectedDate = LocalDate.parse(dateExtra)
                } catch (e: Exception) {
                    selectedDate = LocalDate.of(2026, 10, 15)
                }
            }
            binding.etTitle.setText("Uroczystość Eid")
            updateDateDisplay()
            updateTimeDisplay()
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnSave.setOnClickListener { saveEvent() }

        binding.etDate.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                    updateDateDisplay()
                },
                selectedDate.year,
                selectedDate.monthValue - 1,
                selectedDate.dayOfMonth
            )
            datePicker.show()
        }

        binding.etTime.setOnClickListener {
            val timePicker = TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    selectedTime = LocalTime.of(hourOfDay, minute)
                    updateTimeDisplay()
                },
                selectedTime.hour,
                selectedTime.minute,
                true
            )
            timePicker.show()
        }

        binding.cardThemeGregorian.setOnClickListener { updateThemeVisuals("gregorian") }
        binding.cardThemeIslamic.setOnClickListener { updateThemeVisuals("islamic") }
        binding.cardThemeJewish.setOnClickListener { updateThemeVisuals("jewish") }
        binding.cardThemeHindu.setOnClickListener { updateThemeVisuals("hindu") }
        binding.cardThemeChinese.setOnClickListener { updateThemeVisuals("chinese") }
    }

    private fun updateDateDisplay() {
        binding.etDate.setText(selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")))
    }

    private fun updateTimeDisplay() {
        binding.etTime.setText(selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")))
    }

    private fun updateThemeVisuals(themeKey: String) {
        selectedThemeKey = themeKey
        val theme = ThemeManager.getTheme(themeKey)

        binding.toolbarAddEvent.setBackgroundColor(theme.primaryColor.toInt())
        binding.rootAddEvent.setBackgroundColor(theme.backgroundColor.toInt())

        if (theme.topDecorationResId != null) {
            binding.imgTopDecoration.setImageResource(theme.topDecorationResId)
            binding.imgTopDecoration.visibility = View.VISIBLE
        } else {
            binding.imgTopDecoration.visibility = View.GONE
        }

        if (theme.bottomDecorationResId != null) {
            binding.imgBottomDecoration.setImageResource(theme.bottomDecorationResId)
            binding.imgBottomDecoration.visibility = View.VISIBLE
        } else {
            binding.imgBottomDecoration.visibility = View.GONE
        }

        // Highlight selected theme card border
        val unselectedStroke = 0
        val selectedStroke = 6

        binding.cardThemeGregorian.strokeWidth = if (themeKey == "gregorian") selectedStroke else unselectedStroke
        binding.cardThemeIslamic.strokeWidth = if (themeKey == "islamic") selectedStroke else unselectedStroke
        binding.cardThemeJewish.strokeWidth = if (themeKey == "jewish") selectedStroke else unselectedStroke
        binding.cardThemeHindu.strokeWidth = if (themeKey == "hindu") selectedStroke else unselectedStroke
        binding.cardThemeChinese.strokeWidth = if (themeKey == "chinese") selectedStroke else unselectedStroke
    }

    private fun loadEventToEdit(id: Long) {
        lifecycleScope.launch {
            val event = db.eventDao().getEventById(id)
            if (event != null) {
                binding.tvHeaderTitle.text = "Edytuj Wydarzenie"
                binding.etTitle.setText(event.title)
                binding.etDescription.setText(event.description)
                binding.etLocation.setText(event.location)
                try {
                    selectedDate = LocalDate.parse(event.date)
                } catch (e: Exception) {}
                try {
                    selectedTime = LocalTime.parse(event.time)
                } catch (e: Exception) {}
                updateDateDisplay()
                updateTimeDisplay()
                updateThemeVisuals(event.themeKey)
            }
        }
    }

    private fun saveEvent() {
        val title = binding.etTitle.text.toString().trim()
        if (title.isEmpty()) {
            Toast.makeText(this, "Wprowadź tytuł wydarzenia!", Toast.LENGTH_SHORT).show()
            return
        }

        val description = binding.etDescription.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val dateStr = selectedDate.toString()
        val timeStr = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm"))

        val event = Event(
            id = if (eventIdToEdit != -1L) eventIdToEdit else 0L,
            title = title,
            description = description,
            date = dateStr,
            time = timeStr,
            themeKey = selectedThemeKey,
            location = location
        )

        lifecycleScope.launch {
            if (eventIdToEdit != -1L) {
                db.eventDao().updateEvent(event)
                Toast.makeText(this@AddEditEventActivity, "Zaktualizowano wydarzenie!", Toast.LENGTH_SHORT).show()
            } else {
                db.eventDao().insertEvent(event)
                Toast.makeText(this@AddEditEventActivity, "Zapisano wydarzenie!", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }
}

package com.example.kalendarz

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kalendarz.data.db.AppDatabase
import com.example.kalendarz.data.db.Event
import com.example.kalendarz.data.db.Holiday
import com.example.kalendarz.data.net.HolidayRepository
import com.example.kalendarz.data.prefs.AppPreferences
import com.example.kalendarz.databinding.ActivityMainBinding
import com.example.kalendarz.ui.AddEditEventActivity
import com.example.kalendarz.ui.DayDetailsBottomSheet
import com.example.kalendarz.ui.DateConverterDialog
import com.example.kalendarz.ui.SettingsActivity
import com.example.kalendarz.ui.adapters.CalendarGridAdapter
import com.example.kalendarz.ui.adapters.DayCell
import com.example.kalendarz.util.ThemeManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appPrefs: AppPreferences
    private lateinit var db: AppDatabase
    private lateinit var holidayRepository: HolidayRepository
    private lateinit var calendarAdapter: CalendarGridAdapter

    private var currentYearMonth: YearMonth = YearMonth.of(2026, 10) // Default matching Screenshot 1
    private var allUserEvents: List<Event> = emptyList()
    private var allHolidays: List<Holiday> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        appPrefs = AppPreferences(this)
        db = AppDatabase.getDatabase(this)
        holidayRepository = HolidayRepository(db.holidayDao())

        setupUI()
        observeData()
        syncHolidays()
    }

    override fun onResume() {
        super.onResume()
        applyTheme()
        refreshCalendarGrid()
    }

    private fun setupUI() {
        val themeConfig = ThemeManager.getTheme(appPrefs.activeTheme)
        calendarAdapter = CalendarGridAdapter(emptyList(), themeConfig) { dayCell ->
            val bottomSheet = DayDetailsBottomSheet.newInstance(dayCell.dateString)
            bottomSheet.show(supportFragmentManager, "DayDetailsBottomSheet")
        }

        binding.rvCalendarGrid.layoutManager = GridLayoutManager(this, 7)
        binding.rvCalendarGrid.adapter = calendarAdapter

        binding.btnPrevMonth.setOnClickListener {
            currentYearMonth = currentYearMonth.minusMonths(1)
            updateMonthHeader()
            refreshCalendarGrid()
        }

        binding.btnNextMonth.setOnClickListener {
            currentYearMonth = currentYearMonth.plusMonths(1)
            updateMonthHeader()
            refreshCalendarGrid()
        }

        binding.fabAddEvent.setOnClickListener {
            val intent = Intent(this, AddEditEventActivity::class.java)
            startActivity(intent)
        }

        binding.btnConverter.setOnClickListener {
            val dialog = DateConverterDialog.newInstance(LocalDate.now().toString())
            dialog.show(supportFragmentManager, "DateConverterDialog")
        }

        binding.btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        updateMonthHeader()
    }

    private fun applyTheme() {
        val themeConfig = ThemeManager.getTheme(appPrefs.activeTheme)

        binding.main.setBackgroundColor(themeConfig.backgroundColor.toInt())
        binding.headerTopBar.setBackgroundColor(themeConfig.primaryColor.toInt())
        binding.tvAppTitle.text = "Główny Kalendarz - ${getMonthNamePolish(currentYearMonth.monthValue)} ${currentYearMonth.year}"
        binding.tvMonthYear.setTextColor(themeConfig.textColor.toInt())
        binding.btnPrevMonth.setColorFilter(themeConfig.textColor.toInt())
        binding.btnNextMonth.setColorFilter(themeConfig.textColor.toInt())
        binding.fabAddEvent.backgroundTintList = ColorStateList.valueOf(themeConfig.primaryColor.toInt())

        // Ensure week day headers have high contrast dark text color
        for (i in 0 until binding.layoutWeekDays.childCount) {
            val child = binding.layoutWeekDays.getChildAt(i)
            if (child is TextView) {
                child.setTextColor(themeConfig.textColor.toInt())
            }
        }
    }

    private fun updateMonthHeader() {
        val monthName = getMonthNamePolish(currentYearMonth.monthValue)
        binding.tvMonthYear.text = "$monthName ${currentYearMonth.year}"
        binding.tvAppTitle.text = "Główny Kalendarz - $monthName ${currentYearMonth.year}"
    }

    private fun getMonthNamePolish(month: Int): String {
        return when (month) {
            1 -> "Styczeń"
            2 -> "Luty"
            3 -> "Marzec"
            4 -> "Kwiecień"
            5 -> "Maj"
            6 -> "Czerwiec"
            7 -> "Lipiec"
            8 -> "Sierpień"
            9 -> "Wrzesień"
            10 -> "Październik"
            11 -> "Listopad"
            12 -> "Grudzień"
            else -> ""
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            db.eventDao().getAllEvents().collectLatest { events ->
                allUserEvents = events
                refreshCalendarGrid()
            }
        }
    }

    private fun syncHolidays() {
        lifecycleScope.launch {
            holidayRepository.syncHolidaysForCountries(currentYearMonth.year, appPrefs.selectedCountries)
            refreshCalendarGrid()
        }
    }

    private fun refreshCalendarGrid() {
        lifecycleScope.launch {
            val monthPrefix = String.format(Locale.US, "%04d-%02d", currentYearMonth.year, currentYearMonth.monthValue)
            allHolidays = holidayRepository.getHolidaysForMonth(monthPrefix, appPrefs.selectedCountries)

            val dayCells = generateDayCellsForMonth(currentYearMonth)
            val themeConfig = ThemeManager.getTheme(appPrefs.activeTheme)
            calendarAdapter.updateDays(dayCells, themeConfig)
        }
    }

    private fun generateDayCellsForMonth(yearMonth: YearMonth): List<DayCell> {
        val cells = mutableListOf<DayCell>()
        val firstDayOfMonth = yearMonth.atDay(1)
        val dayOfWeekOfFirst = firstDayOfMonth.dayOfWeek.value // 1 = Mon, 7 = Sun

        val today = LocalDate.now()

        // Previous month days fill
        val prevMonth = yearMonth.minusMonths(1)
        val prevMonthDays = prevMonth.lengthOfMonth()
        val prevDaysToDisplay = dayOfWeekOfFirst - 1

        for (i in (prevMonthDays - prevDaysToDisplay + 1)..prevMonthDays) {
            val date = prevMonth.atDay(i)
            val dateStr = date.toString()
            cells.add(
                DayCell(
                    dayNumber = i,
                    dateString = dateStr,
                    isCurrentMonth = false,
                    isToday = date == today,
                    holidays = allHolidays.filter { it.date == dateStr },
                    events = allUserEvents.filter { it.date == dateStr }
                )
            )
        }

        // Current month days
        val daysInMonth = yearMonth.lengthOfMonth()
        for (i in 1..daysInMonth) {
            val date = yearMonth.atDay(i)
            val dateStr = date.toString()
            cells.add(
                DayCell(
                    dayNumber = i,
                    dateString = dateStr,
                    isCurrentMonth = true,
                    isToday = date == today,
                    holidays = allHolidays.filter { it.date == dateStr },
                    events = allUserEvents.filter { it.date == dateStr }
                )
            )
        }

        // Next month trailing days to complete 42 cells (7x6 grid)
        val remainingCells = 42 - cells.size
        val nextMonth = yearMonth.plusMonths(1)
        for (i in 1..remainingCells) {
            val date = nextMonth.atDay(i)
            val dateStr = date.toString()
            cells.add(
                DayCell(
                    dayNumber = i,
                    dateString = dateStr,
                    isCurrentMonth = false,
                    isToday = date == today,
                    holidays = allHolidays.filter { it.date == dateStr },
                    events = allUserEvents.filter { it.date == dateStr }
                )
            )
        }

        return cells
    }
}

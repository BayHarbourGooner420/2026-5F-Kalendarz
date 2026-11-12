package com.example.kalendarz.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.kalendarz.R
import com.example.kalendarz.data.db.AppDatabase
import com.example.kalendarz.data.net.HolidayRepository
import com.example.kalendarz.data.prefs.AppPreferences
import com.example.kalendarz.databinding.BottomSheetDayDetailsBinding
import com.example.kalendarz.databinding.ItemEventListBinding
import com.example.kalendarz.util.CalendarConverter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class DayDetailsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetDayDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: AppDatabase
    private lateinit var appPrefs: AppPreferences
    private lateinit var holidayRepository: HolidayRepository

    private var dateString: String = LocalDate.now().toString()

    companion object {
        fun newInstance(dateStr: String): DayDetailsBottomSheet {
            val sheet = DayDetailsBottomSheet()
            val args = Bundle()
            args.putString("DATE_STRING", dateStr)
            sheet.arguments = args
            return sheet
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dateString = arguments?.getString("DATE_STRING") ?: LocalDate.now().toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetDayDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getDatabase(requireContext())
        appPrefs = AppPreferences(requireContext())
        holidayRepository = HolidayRepository(db.holidayDao())

        val date = try {
            LocalDate.parse(dateString)
        } catch (e: Exception) {
            LocalDate.now()
        }

        // Header Title
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("pl"))
        binding.tvSheetDateTitle.text = date.format(formatter)

        // Parallel Dates Summary
        val convertedDates = CalendarConverter.convertDate(date)
        val parallelText = convertedDates
            .filter { it.calendarKey != "gregorian" }
            .joinToString(" | ") { "${it.calendarName.split(" ").first()}: ${it.formattedDate}" }
        binding.tvSheetParallelDates.text = parallelText

        // Setup Add Event Button
        binding.btnAddEventForDay.setOnClickListener {
            val intent = Intent(requireContext(), AddEditEventActivity::class.java)
            intent.putExtra("PRESET_DATE", dateString)
            startActivity(intent)
            dismiss()
        }

        loadHolidaysAndEvents()
    }

    private fun loadHolidaysAndEvents() {
        lifecycleScope.launch {
            // Holidays
            val holidays = holidayRepository.getHolidaysForDate(dateString, appPrefs.selectedCountries)
            binding.containerHolidays.removeAllViews()

            if (holidays.isEmpty()) {
                val tvEmpty = TextView(requireContext()).apply {
                    text = "Brak świąt państwowych w tym dniu."
                    setTextColor(Color.parseColor("#444444"))
                    textSize = 13f
                    setPadding(0, 8, 0, 8)
                }
                binding.containerHolidays.addView(tvEmpty)
            } else {
                for (h in holidays) {
                    val tvHoliday = TextView(requireContext()).apply {
                        text = "${h.flagEmoji} ${h.localName} (${h.name})"
                        setTextColor(Color.parseColor("#111111"))
                        textSize = 14f
                        setPadding(0, 6, 0, 6)
                    }
                    binding.containerHolidays.addView(tvHoliday)
                }
            }

            // User Events
            db.eventDao().getEventsForDate(dateString).collectLatest { events ->
                binding.containerUserEvents.removeAllViews()

                if (events.isEmpty()) {
                    val tvEmpty = TextView(requireContext()).apply {
                        text = "Brak wpisanych wydarzeń."
                        setTextColor(Color.parseColor("#444444"))
                        textSize = 13f
                        setPadding(0, 8, 0, 8)
                    }
                    binding.containerUserEvents.addView(tvEmpty)
                } else {
                    for (event in events) {
                        val itemBinding = ItemEventListBinding.inflate(
                            layoutInflater,
                            binding.containerUserEvents,
                            false
                        )

                        itemBinding.tvEventTitle.text = event.title
                        itemBinding.tvEventTimeLocation.text = "${event.time} | ${event.location.ifEmpty { "Brak lokalizacji" }}"
                        itemBinding.tvEventDescription.text = event.description.ifEmpty { "Brak opisu" }

                        itemBinding.btnEditEvent.setOnClickListener {
                            val intent = Intent(requireContext(), AddEditEventActivity::class.java)
                            intent.putExtra("EVENT_ID", event.id)
                            startActivity(intent)
                            dismiss()
                        }

                        itemBinding.btnDeleteEvent.setOnClickListener {
                            lifecycleScope.launch {
                                db.eventDao().deleteEvent(event)
                                Toast.makeText(requireContext(), "Usunięto wydarzenie", Toast.LENGTH_SHORT).show()
                            }
                        }

                        binding.containerUserEvents.addView(itemBinding.root)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

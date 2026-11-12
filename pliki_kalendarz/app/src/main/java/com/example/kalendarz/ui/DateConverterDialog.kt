package com.example.kalendarz.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.kalendarz.R
import com.example.kalendarz.databinding.DialogDateConverterBinding
import com.example.kalendarz.databinding.ItemConvertedDateBinding
import com.example.kalendarz.util.CalendarConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateConverterDialog : DialogFragment() {

    private var _binding: DialogDateConverterBinding? = null
    private val binding get() = _binding!!

    private var selectedDate: LocalDate = LocalDate.now()

    companion object {
        fun newInstance(dateStr: String): DateConverterDialog {
            val dialog = DateConverterDialog()
            val args = Bundle()
            args.putString("DATE_STRING", dateStr)
            dialog.arguments = args
            return dialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dateStr = arguments?.getString("DATE_STRING") ?: LocalDate.now().toString()
        selectedDate = try {
            LocalDate.parse(dateStr)
        } catch (e: Exception) {
            LocalDate.now()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogDateConverterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnClose.setOnClickListener { dismiss() }

        binding.btnSelectDate.setOnClickListener {
            val picker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                    updateConvertedDates()
                },
                selectedDate.year,
                selectedDate.monthValue - 1,
                selectedDate.dayOfMonth
            )
            picker.show()
        }

        updateConvertedDates()
    }

    private fun updateConvertedDates() {
        binding.btnSelectDate.text = "Wybierz Datę: ${selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}"
        binding.containerConvertedDates.removeAllViews()

        val convertedList = CalendarConverter.convertDate(selectedDate)

        for (item in convertedList) {
            val itemBinding = ItemConvertedDateBinding.inflate(
                layoutInflater,
                binding.containerConvertedDates,
                false
            )

            itemBinding.tvCalendarSystemTitle.text = item.calendarName
            itemBinding.tvFormattedConvertedDate.text = item.formattedDate
            itemBinding.tvConvertedDetails.text = item.details

            val iconRes = when (item.iconResName) {
                "ic_islamic" -> R.drawable.ic_islamic
                "ic_jewish" -> R.drawable.ic_jewish
                "ic_chinese" -> R.drawable.ic_chinese
                "ic_hindu" -> R.drawable.ic_hindu
                else -> R.drawable.ic_calendar
            }
            itemBinding.imgCalendarSystemIcon.setImageResource(iconRes)

            binding.containerConvertedDates.addView(itemBinding.root)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

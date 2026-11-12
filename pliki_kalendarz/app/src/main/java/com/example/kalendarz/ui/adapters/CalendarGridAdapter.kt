package com.example.kalendarz.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kalendarz.databinding.ItemCalendarDayBinding
import com.example.kalendarz.util.ThemeConfig

class CalendarGridAdapter(
    private var days: List<DayCell> = emptyList(),
    private var themeConfig: ThemeConfig,
    private val onDayClick: (DayCell) -> Unit
) : RecyclerView.Adapter<CalendarGridAdapter.DayViewHolder>() {

    fun updateDays(newDays: List<DayCell>, newTheme: ThemeConfig) {
        this.days = newDays
        this.themeConfig = newTheme
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val binding = ItemCalendarDayBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(days[position])
    }

    override fun getItemCount(): Int = days.size

    inner class DayViewHolder(private val binding: ItemCalendarDayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dayCell: DayCell) {
            binding.tvDayNumber.text = dayCell.dayNumber.toString()

            if (dayCell.isCurrentMonth) {
                binding.tvDayNumber.setTextColor(themeConfig.textColor.toInt())
                binding.cardDay.setCardBackgroundColor(Color.WHITE)
                binding.cardDay.alpha = 1.0f
            } else {
                binding.tvDayNumber.setTextColor(Color.parseColor("#777777"))
                binding.cardDay.setCardBackgroundColor(Color.parseColor("#F5F5F5"))
                binding.cardDay.alpha = 0.6f
            }

            if (dayCell.isToday) {
                binding.cardDay.setStrokeColor(themeConfig.accentColor.toInt())
                binding.cardDay.strokeWidth = 4
            } else {
                binding.cardDay.setStrokeColor(Color.parseColor("#CCCCCC"))
                binding.cardDay.strokeWidth = 1
            }

            // Holiday display
            if (dayCell.holidays.isNotEmpty()) {
                val primaryHoliday = dayCell.holidays.first()
                binding.layoutHolidayInfo.visibility = View.VISIBLE
                binding.tvFlag.text = primaryHoliday.flagEmoji
                binding.tvHolidayName.text = primaryHoliday.localName.ifEmpty { primaryHoliday.name }
            } else {
                binding.layoutHolidayInfo.visibility = View.GONE
            }

            // User event indicator dot
            if (dayCell.events.isNotEmpty()) {
                binding.indicatorEvent.visibility = View.VISIBLE
            } else {
                binding.indicatorEvent.visibility = View.GONE
            }

            binding.root.setOnClickListener {
                onDayClick(dayCell)
            }
        }
    }
}

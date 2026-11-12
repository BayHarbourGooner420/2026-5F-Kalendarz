package com.example.kalendarz.util

import android.icu.util.ChineseCalendar
import android.icu.util.HebrewCalendar
import android.icu.util.IndianCalendar
import android.icu.util.IslamicCalendar
import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class ConvertedDate(
    val calendarKey: String,
    val calendarName: String,
    val formattedDate: String,
    val details: String,
    val iconResName: String
)

object CalendarConverter {

    fun convertDate(date: LocalDate): List<ConvertedDate> {
        val list = mutableListOf<ConvertedDate>()

        // 1. Gregorian Calendar
        val gregFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("pl"))
        list.add(
            ConvertedDate(
                calendarKey = "gregorian",
                calendarName = "Kalendarz Gregoriański",
                formattedDate = date.format(gregFormatter),
                details = "Standardowy kalendarz słoneczny strefy zachodniej.",
                iconResName = "ic_calendar"
            )
        )

        // 2. Islamic Calendar (Hijri)
        val islamicDateStr = try {
            val hijrahDate = HijrahDate.from(date)
            val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy 'AH'", Locale("pl"))
            hijrahDate.format(formatter)
        } catch (e: Exception) {
            try {
                val cal = IslamicCalendar().apply {
                    set(date.year, date.monthValue - 1, date.dayOfMonth)
                }
                val year = cal.get(IslamicCalendar.YEAR)
                val month = cal.get(IslamicCalendar.MONTH)
                val day = cal.get(IslamicCalendar.DAY_OF_MONTH)
                val monthNames = arrayOf(
                    "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
                    "Jumada al-Awwal", "Jumada al-Thani", "Rajab", "Sha'ban",
                    "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
                )
                "$day ${monthNames.getOrElse(month) { "" }} $year AH"
            } catch (ex: Exception) {
                "Kalendarz Hijri"
            }
        }
        list.add(
            ConvertedDate(
                calendarKey = "islamic",
                calendarName = "Kalendarz Islamski (Hijri)",
                formattedDate = islamicDateStr,
                details = "Kalendarz księżycowy oparty na cyklach Księżyca (12 miesięcy).",
                iconResName = "ic_islamic"
            )
        )

        // 3. Hebrew / Jewish Calendar
        val hebrewDateStr = try {
            val cal = HebrewCalendar().apply {
                set(date.year, date.monthValue - 1, date.dayOfMonth)
            }
            val year = cal.get(HebrewCalendar.YEAR)
            val month = cal.get(HebrewCalendar.MONTH)
            val day = cal.get(HebrewCalendar.DAY_OF_MONTH)
            val monthNames = arrayOf(
                "Tishrei", "Chesvan", "Kislev", "Tevet", "Shevat", "Adar",
                "Adar II", "Nisan", "Iyar", "Sivan", "Tammuz", "Av", "Elul"
            )
            "$day ${monthNames.getOrElse(month) { "" }} $year"
        } catch (e: Exception) {
            "Kalendarz Żydowski"
        }
        list.add(
            ConvertedDate(
                calendarKey = "jewish",
                calendarName = "Kalendarz Żydowski",
                formattedDate = hebrewDateStr,
                details = "Kalendarz księżycowo-słoneczny z miesiącami przestępnymi.",
                iconResName = "ic_jewish"
            )
        )

        // 4. Chinese Lunar Calendar
        val chineseDateStr = try {
            val cal = ChineseCalendar().apply {
                set(date.year, date.monthValue - 1, date.dayOfMonth)
            }
            val year = cal.get(ChineseCalendar.YEAR)
            val month = cal.get(ChineseCalendar.MONTH) + 1
            val day = cal.get(ChineseCalendar.DAY_OF_MONTH)
            val zodiac = arrayOf(
                "Szczura", "Wołu", "Tygrysa", "Królika", "Smoka", "Węża",
                "Konia", "Kozy", "Małpy", "Koguta", "Psa", "Świni"
            )
            val zodiacIndex = (date.year - 4) % 12
            val zodiacName = zodiac.getOrElse(if (zodiacIndex < 0) zodiacIndex + 12 else zodiacIndex) { "" }
            "Miesiąc $month, Dzień $day (Rok $zodiacName)"
        } catch (e: Exception) {
            "Kalendarz Chiński"
        }
        list.add(
            ConvertedDate(
                calendarKey = "chinese",
                calendarName = "Kalendarz Chiński",
                formattedDate = chineseDateStr,
                details = "Tradycyjny kalendarz lunisolarny określający cykle rolnicze i święta.",
                iconResName = "ic_chinese"
            )
        )

        // 5. Hindu / Saka Calendar
        val hinduDateStr = try {
            val cal = IndianCalendar().apply {
                set(date.year, date.monthValue - 1, date.dayOfMonth)
            }
            val year = cal.get(IndianCalendar.YEAR)
            val month = cal.get(IndianCalendar.MONTH)
            val day = cal.get(IndianCalendar.DAY_OF_MONTH)
            val monthNames = arrayOf(
                "Chaitra", "Vaisakha", "Jyaistha", "Asadha", "Sravana", "Bhadra",
                "Asvina", "Kartika", "Agrahayana", "Pausa", "Magha", "Phalguna"
            )
            "$day ${monthNames.getOrElse(month) { "" }} $year Saka"
        } catch (e: Exception) {
            "Kalendarz Hinduski"
        }
        list.add(
            ConvertedDate(
                calendarKey = "hindu",
                calendarName = "Kalendarz Hinduski (Saka)",
                formattedDate = hinduDateStr,
                details = "Oficjalny indyjski cykl kalendarzowy Saka.",
                iconResName = "ic_hindu"
            )
        )

        return list
    }
}

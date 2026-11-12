package com.example.kalendarz.data.net

import com.example.kalendarz.data.db.Holiday
import com.example.kalendarz.data.db.HolidayDao
import com.example.kalendarz.util.FlagUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class HolidayRepository(private val holidayDao: HolidayDao) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    suspend fun syncHolidaysForCountries(year: Int, countryCodes: Set<String>) = withContext(Dispatchers.IO) {
        ensureDefaultHolidaysLoaded()

        for (countryCode in countryCodes) {
            try {
                val url = "https://date.nager.at/api/v3/PublicHolidays/$year/$countryCode"
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val json = response.body?.string()
                    if (!json.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<HolidayDto>>() {}.type
                        val dtos: List<HolidayDto> = gson.fromJson(json, listType)

                        val entities = dtos.map { dto ->
                            Holiday(
                                id = "${dto.countryCode}-${dto.date}-${dto.name}",
                                date = dto.date,
                                localName = dto.localName,
                                name = dto.name,
                                countryCode = dto.countryCode,
                                flagEmoji = FlagUtils.getFlagEmoji(dto.countryCode)
                            )
                        }
                        holidayDao.insertHolidays(entities)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun ensureDefaultHolidaysLoaded() {
        val existing = holidayDao.getAllHolidays()
        if (existing.isNotEmpty()) return

        val defaultList = listOf(
            // October 2026 Holidays matching Screenshot 1
            Holiday("NG-2026-10-01-Independence Day", "2026-10-01", "Independence Day", "Independence Day Nigeria", "NG", FlagUtils.getFlagEmoji("NG")),
            Holiday("US-2026-10-12-Columbus Day", "2026-10-12", "Columbus Day", "Columbus Day USA", "US", FlagUtils.getFlagEmoji("US")),
            Holiday("TR-2026-10-29-Republic Day", "2026-10-29", "Republic Day", "Republic Day Turkie", "TR", FlagUtils.getFlagEmoji("TR")),

            // Other default holidays
            Holiday("PL-2026-11-11-Independence Day", "2026-11-11", "Święto Niepodległości", "Independence Day Poland", "PL", FlagUtils.getFlagEmoji("PL")),
            Holiday("DE-2026-10-03-German Unity Day", "2026-10-03", "Tag der Deutschen Einheit", "German Unity Day", "DE", FlagUtils.getFlagEmoji("DE")),
            Holiday("JP-2026-11-03-Culture Day", "2026-11-03", "Bunka no Hi", "Culture Day Japan", "JP", FlagUtils.getFlagEmoji("JP")),
            Holiday("IN-2026-10-02-Gandhi Jayanti", "2026-10-02", "Gandhi Jayanti", "Gandhi Jayanti India", "IN", FlagUtils.getFlagEmoji("IN")),
            Holiday("IN-2026-11-08-Diwali", "2026-11-08", "Diwali", "Diwali Festival India", "IN", FlagUtils.getFlagEmoji("IN"))
        )
        holidayDao.insertHolidays(defaultList)
    }

    suspend fun getHolidaysForMonth(monthPrefix: String, enabledCountries: Set<String>): List<Holiday> = withContext(Dispatchers.IO) {
        ensureDefaultHolidaysLoaded()
        val allForMonth = holidayDao.getHolidaysForMonth(monthPrefix)
        return@withContext allForMonth.filter { enabledCountries.contains(it.countryCode) }
    }

    suspend fun getHolidaysForDate(date: String, enabledCountries: Set<String>): List<Holiday> = withContext(Dispatchers.IO) {
        ensureDefaultHolidaysLoaded()
        val allForDate = holidayDao.getHolidaysForDate(date)
        return@withContext allForDate.filter { enabledCountries.contains(it.countryCode) }
    }
}

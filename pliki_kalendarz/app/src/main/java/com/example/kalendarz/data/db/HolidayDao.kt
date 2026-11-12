package com.example.kalendarz.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HolidayDao {
    @Query("SELECT * FROM holidays WHERE date LIKE :monthPrefix || '%'")
    suspend fun getHolidaysForMonth(monthPrefix: String): List<Holiday>

    @Query("SELECT * FROM holidays WHERE date = :date")
    suspend fun getHolidaysForDate(date: String): List<Holiday>

    @Query("SELECT * FROM holidays")
    suspend fun getAllHolidays(): List<Holiday>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHolidays(holidays: List<Holiday>): List<Long>

    @Query("DELETE FROM holidays WHERE countryCode = :countryCode")
    suspend fun deleteHolidaysForCountry(countryCode: String): Int

    @Query("DELETE FROM holidays")
    suspend fun clearAllHolidays(): Int
}

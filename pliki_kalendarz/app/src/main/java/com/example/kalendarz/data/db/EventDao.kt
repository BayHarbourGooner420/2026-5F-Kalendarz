package com.example.kalendarz.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY date ASC, time ASC")
    fun getAllEvents(): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE date = :date ORDER BY time ASC")
    fun getEventsForDate(date: String): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE date LIKE :monthPrefix || '%' ORDER BY date ASC, time ASC")
    fun getEventsForMonth(monthPrefix: String): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: Long): Event?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event): Long

    @Update
    suspend fun updateEvent(event: Event): Int

    @Delete
    suspend fun deleteEvent(event: Event): Int
}

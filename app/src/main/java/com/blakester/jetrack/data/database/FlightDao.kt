package com.blakester.jetrack.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.blakester.jetrack.data.model.AverageDuration

@Dao
interface FlightDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlight(flight: Flight)

    @Query("SELECT * FROM flights WHERE flightCode = :flightCode")
    suspend fun getFlightsByCode(flightCode: String): List<Flight>

    @Query("SELECT * FROM flights WHERE date = :date")
    suspend fun getFlightsByDate(date: String): List<Flight>

    @Query("SELECT AVG(actualDuration) FROM flights")
    suspend fun getAverageDuration(): Double

    @Query("SELECT * FROM flights WHERE flightCode = :flightCode ORDER BY date DESC LIMIT 7")
    suspend fun getLast7Days(flightCode: String): List<Flight>

    @Query("SELECT flightCode, AVG(actualDuration) as averageTime FROM flights GROUP BY flightCode")
    fun getAllAverageDurations(): LiveData<List<AverageDuration>>

    @Query("SELECT COUNT(*) FROM flights WHERE flightCode = :flightCode")
    suspend fun getFlightCountForCode(flightCode: String): Int

    @Query("SELECT COUNT(*) FROM flights WHERE flightCode = :code AND date = :date")
    suspend fun exists(code: String, date: String): Int
}
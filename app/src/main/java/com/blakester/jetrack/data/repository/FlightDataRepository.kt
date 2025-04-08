package com.blakester.jetrack.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import com.blakester.jetrack.data.database.FlightDao
import com.blakester.jetrack.data.database.Flight
import com.blakester.jetrack.data.model.AverageDuration
import com.blakester.jetrack.data.network.FlightStatsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class FlightDataRepository(
    private val api: FlightStatsApi,
    private val dao: FlightDao
) {
    val allAverages: LiveData<List<AverageDuration>> = dao.getAllAverageDurations()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchAndStoreFlightDuration(
        flightCode: String,
        carrier: String,
        flightNumber: String,
        year: Int,
        month: Int,
        day: Int,
        appId: String,
        appKey: String
    ) = withContext(Dispatchers.IO) {
        try {
            val response = api.getFlightStatus(
                carrier,
                flightNumber,
                year,
                month,
                day,
                appId,
                appKey,
                true // UTC = true
            )

            Log.d("resp", response.toString())

            val flightStatus = response.flightStatuses.firstOrNull()
            val operationalTimes = flightStatus?.operationalTimes

            val departureTimeStr = operationalTimes?.actualGateDeparture?.dateLocal
            val arrivalTimeStr = operationalTimes?.actualGateArrival?.dateLocal

            val schedledDepartureStr = operationalTimes?.scheduledGateDeparture?.dateLocal
            val scheduledArrivalStr = operationalTimes?.scheduledGateArrival?.dateLocal

            if (departureTimeStr != null && arrivalTimeStr != null) {
                val formatter = DateTimeFormatter.ISO_DATE_TIME
                val departure = LocalDateTime.parse(departureTimeStr, formatter)
                val arrival = LocalDateTime.parse(arrivalTimeStr, formatter)
                val scheduledDeparture = LocalDateTime.parse(schedledDepartureStr, formatter)
                val scheduledArrival = LocalDateTime.parse(scheduledArrivalStr, formatter)
                val durationInMinutes = Duration.between(departure, arrival).toMinutes().toInt()
                val scheduleInMinutes = Duration.between(scheduledDeparture, scheduledArrival).toMinutes().toInt()
                val date = "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
                val alreadyExists = dao.exists(flightCode, date) > 0
                if (alreadyExists) {
                    Log.d("FlightRepo", "Skipping $flightCode on $date (already in DB)")
                    return@withContext
                }
                val entity = Flight(
                    flightCode = flightCode,
                    date = date,
                    departureTime = departure.toLocalTime().toString(),
                    arrivalTime = arrival.toLocalTime().toString(),
                    scheduledDuration = scheduleInMinutes,
                    actualDuration = durationInMinutes
                )
                dao.insertFlight(entity)
                Log.d("FlightRepo", "Inserting ${date} flights")
            }
        } catch (e: Exception) {
            Log.d("the error", e.localizedMessage)
            e.printStackTrace()
            // Optionally log error
        }
    }

    suspend fun getAverageDuration(flightCode: String): Double {
        val flights = dao.getLast7Days(flightCode)
        return flights.map { it.actualDuration }.average()
    }

    suspend fun getFlightCount(flightCode: String): Int {
        return dao.getFlightCountForCode(flightCode)
    }
}
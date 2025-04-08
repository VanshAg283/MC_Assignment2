package com.blakester.jetrack.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blakester.jetrack.data.model.AverageDuration
import com.blakester.jetrack.data.repository.FlightDataRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

class FlightDataViewModel(
    private val repository: FlightDataRepository
) : ViewModel() {

    private val _durationStatus = MutableLiveData<String>()
    val durationStatus: LiveData<String> get() = _durationStatus

    private val _averageDuration = MutableLiveData<Double>()
    val averageDuration: LiveData<Double> get() = _averageDuration

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchAndStoreFlightDuration(
        flightCode: String,
        carrier: String,
        flightNumber: String,
        year: Int,
        month: Int,
        day: Int,
        appId: String,
        appKey: String
    ) {
        viewModelScope.launch {
            try {
                repository.fetchAndStoreFlightDuration(
                    flightCode, carrier, flightNumber, year, month, day, appId, appKey
                )
                _durationStatus.value = "Flight duration stored successfully."
            } catch (e: Exception) {
                _durationStatus.value = "Error storing duration: ${e.localizedMessage}"
            }
        }
    }

    fun computeAverageDuration(flightCode: String) {
        viewModelScope.launch {
            try {
                val avg = repository.getAverageDuration(flightCode)
                _averageDuration.value = avg
            } catch (e: Exception) {
                _durationStatus.value = "Error fetching average: ${e.localizedMessage}"
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun preloadDefaultFlights(appId: String, appKey: String) {
        val flights = listOf(
            Triple("IGO6107", "IGO", "6107"),
            Triple("AIC2963", "AIC", "2963"),
            Triple("AKJ1411", "AKJ", "1411")
        )

        for ((code, carrier, number) in flights) {
            preloadLast7DaysData(code, carrier, number, appId, appKey)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun preloadLast7DaysData(
        flightCode: String,
        carrier: String,
        flightNumber: String,
        appId: String,
        appKey: String
    ) {
        viewModelScope.launch {
            val count = repository.getFlightCount(flightCode)
            if (count >= 7) {
                _durationStatus.value = "Flight data already available."
                return@launch
            }

            val today = LocalDate.now()
            for (i in 1..7) {
                val date = today.minusDays(i.toLong())
                try {
                    repository.fetchAndStoreFlightDuration(
                        flightCode,
                        carrier,
                        flightNumber,
                        date.year,
                        date.monthValue,
                        date.dayOfMonth,
                        appId,
                        appKey
                    )
                } catch (e: Exception) {
                    _durationStatus.value = "Error on ${date}: ${e.localizedMessage}"
                }
            }
            _durationStatus.value = "Preload complete."
        }
    }

    val allAverages: LiveData<List<AverageDuration>> = repository.allAverages
}
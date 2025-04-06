package com.blakester.jetrack.viewmodel

import androidx.lifecycle.*
import com.blakester.jetrack.data.model.FlightState
import com.blakester.jetrack.data.repository.FlightRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class FlightViewModel(
    private val repository: FlightRepository = FlightRepository()
) : ViewModel() {

    private val _flightState = MutableStateFlow(FlightState())
    val flightState = _flightState.asStateFlow()

    private var trackingJob: Job? = null

    fun fetchFlightLocationEveryMinute(flightNumber: String, context: android.content.Context) {
        // Stop previous job if running
        trackingJob?.cancel()

        if (flightNumber.isBlank()) {
            _flightState.value = FlightState(error = "Flight number cannot be empty")
            return
        }

        trackingJob = viewModelScope.launch {
            while (isActive) {
                _flightState.update { it.copy(loading = true) }
                val result = repository.getFlightStateByCallsign(flightNumber.trim().uppercase(), context)
                _flightState.update {
                    result.copy(secondsUntilRefresh = it.secondsUntilRefresh, loading = false)
                }

                if (result.error != null) {
                    // Stop tracking automatically if error occurs
                    stopTracking()
                    break
                }

                for (i in 59 downTo 0) {
                    delay(1000)
                    _flightState.update { it.copy(secondsUntilRefresh = i) }
                }
            }
        }
    }

    fun stopTracking() {
        trackingJob?.cancel()
        trackingJob = null
        _flightState.update { it.copy(secondsUntilRefresh = 0, loading = false) }
    }

    fun clearLocation() {
        _flightState.value = _flightState.value.copy(
            latitude = null,
            longitude = null,
            error = null
        )
    }
}
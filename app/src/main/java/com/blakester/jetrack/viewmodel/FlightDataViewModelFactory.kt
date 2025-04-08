package com.blakester.jetrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.blakester.jetrack.data.repository.FlightDataRepository

class FlightDataViewModelFactory(
    private val repository: FlightDataRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlightDataViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FlightDataViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
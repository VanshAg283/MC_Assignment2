package com.blakester.jetrack.data.repository

import android.content.Context
import android.util.Log
import com.blakester.jetrack.data.model.FlightState
import com.blakester.jetrack.data.network.FlightApiService
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class FlightRepository {
    suspend fun getFlightStateByCallsign(callsign: String, context: Context): FlightState {
        return withContext(Dispatchers.IO) {
            try {
                val response = FlightApiService.api.getStates()

                // 🔍 Print full response for debugging
                println("Full response: time=${response.time}, states=${response.states}")

                // Optionally save JSON response
                // saveResponseAsJson(response, context)

                val matching = response.states.find { state ->
                    state[1]?.toString()?.trim()?.equals(callsign, ignoreCase = true) == true
                }

                if (matching != null) {
                    FlightState(
                        callsign = matching[1]?.toString()?.trim(),
                        origin_country = matching[2]?.toString(),
                        time_position = matching[3]?.toString()?.toDoubleOrNull()?.toLong(),
                        longitude = matching[5]?.toString()?.toDoubleOrNull(),
                        latitude = matching[6]?.toString()?.toDoubleOrNull(),
                        baro_altitude = matching[7]?.toString()?.toDoubleOrNull(),
                        velocity = matching[9]?.toString()?.toDoubleOrNull(),
                        true_track = matching[10]?.toString()?.toDoubleOrNull()
                    )
                } else {
                    FlightState(error = "Flight not found.")
                }
            } catch (e: Exception) {
                FlightState(error = "Error: ${e.message}")
            }
        }
    }

    private fun saveResponseAsJson(data: Any, context: Context) {
        try {
            val moshi = Moshi.Builder()
                .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
                .build()
            val jsonAdapter = moshi.adapter(Any::class.java)
            val json = jsonAdapter.toJson(data)

            val file = File(context.filesDir, "flight_response.json")
            file.writeText(json)
            Log.d("FlightRepository", "Saved JSON to ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e("FlightRepository", "Failed to write JSON: ${e.message}")
        }
    }
}
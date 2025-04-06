package com.blakester.jetrack.data.model

data class FlightState(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val callsign: String? = null,
    val origin_country: String? = null,
    val baro_altitude: Double? = null,
    val velocity: Double? = null,
    val true_track: Double? = null,
    val time_position:Long? = null,
    val secondsUntilRefresh: Int = 0, // ⏱ countdown timer
    val loading: Boolean = false,
    val error: String? = null
)
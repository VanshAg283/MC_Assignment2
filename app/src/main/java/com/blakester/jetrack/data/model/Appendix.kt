package com.blakester.jetrack.data.model

import com.google.gson.annotations.SerializedName

data class Appendix(
    @SerializedName("airlines")
    val airlines: List<Airline> = emptyList(),

    @SerializedName("airports")
    val airports: List<Airport> = emptyList()
)

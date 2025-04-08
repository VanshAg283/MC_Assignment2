package com.blakester.jetrack.data.model

import com.google.gson.annotations.SerializedName

data class FlightStatus(
    @SerializedName("flightId")
    val flightId: Long,

    @SerializedName("carrierFsCode")
    val carrierFsCode: String,

    @SerializedName("flightNumber")
    val flightNumber: String,

    @SerializedName("departureAirportFsCode")
    val departureAirportFsCode: String,

    @SerializedName("arrivalAirportFsCode")
    val arrivalAirportFsCode: String,

    @SerializedName("departureDate")
    val departureDate: DateInfo,

    @SerializedName("arrivalDate")
    val arrivalDate: DateInfo,

    @SerializedName("operationalTimes")
    val operationalTimes: OperationalTimes
)

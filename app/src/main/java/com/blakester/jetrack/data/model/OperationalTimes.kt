package com.blakester.jetrack.data.model

import com.google.gson.annotations.SerializedName

data class OperationalTimes(
    @SerializedName("scheduledGateDeparture")
    val scheduledGateDeparture: DateInfo?= null,

    @SerializedName("estimatedGateDeparture")
    val actualGateDeparture: DateInfo? = null,

    @SerializedName("scheduledGateArrival")
    val scheduledGateArrival: DateInfo?= null,

    @SerializedName("estimatedGateArrival")
    val actualGateArrival: DateInfo? = null
)

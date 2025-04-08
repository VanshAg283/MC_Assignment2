package com.blakester.jetrack.data.model

import com.google.gson.annotations.SerializedName

data class DateInfo(
    @SerializedName("dateUtc")
    val dateUtc: String,

    @SerializedName("dateLocal")
    val dateLocal: String
)

package com.blakester.jetrack.data.model

import com.google.gson.annotations.SerializedName

data class Airport(
    @SerializedName("fs")
    val fs: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("city")
    val city: String
)

package com.blakester.jetrack.data.model

import com.google.gson.annotations.SerializedName

data class Airline(
    @SerializedName("fs")
    val fs: String,

    @SerializedName("name")
    val name: String
)
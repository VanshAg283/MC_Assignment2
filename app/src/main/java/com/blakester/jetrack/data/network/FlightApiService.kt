package com.blakester.jetrack.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface FlightApi {
    @GET("states/all")
    suspend fun getStates(): FlightApiResponse
}

data class FlightApiResponse(
    val time: Long,
    val states: List<List<Any?>>
)

object FlightApiService {
    val api: FlightApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://opensky-network.org/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FlightApi::class.java)
    }
}
package com.blakester.jetrack.data.network

import com.blakester.jetrack.data.model.Appendix
import com.blakester.jetrack.data.model.FlightStatus
import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Define the API endpoints
interface FlightStatsApi {
    @GET("flex/flightstatus/rest/v2/json/flight/status/{carrier}/{flightNumber}/arr/{year}/{month}/{day}")
    suspend fun getFlightStatus(
        @Path("carrier") carrier: String,
        @Path("flightNumber") flightNumber: String,
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Path("day") day: Int,
        @Query("appId") appId: String,
        @Query("appKey") appKey: String,
        @Query("utc") utc: Boolean = true,
    ) : FlightStatsApiResponse
}

data class FlightStatsApiResponse(
    @SerializedName("flightStatuses")
    val flightStatuses: List<FlightStatus> = emptyList(),

    @SerializedName("appendix")
    val appendix: Appendix? = null
)

object FlightStatsApiService {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.flightstats.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: FlightStatsApi = retrofit.create(FlightStatsApi::class.java)
}
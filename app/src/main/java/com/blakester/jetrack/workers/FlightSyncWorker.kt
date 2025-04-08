package com.blakester.jetrack.workers

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.*
import com.blakester.jetrack.Keys
import com.blakester.jetrack.data.database.FlightDatabase
import com.blakester.jetrack.data.network.FlightStatsApi
import com.blakester.jetrack.data.network.FlightStatsApiService
import com.blakester.jetrack.data.repository.FlightDataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.*
import java.util.concurrent.TimeUnit

class FlightSyncWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
//        Log.d("FlightWorker", "Raw inputData: $inputData")
//        Log.d("FlightWorker", "AppId: ${inputData.getString("APP_ID")}")
//        Log.d("FlightWorker", "AppKey: ${inputData.getString("APP_KEY")}")

        val appId = inputData.getString("APP_ID") ?: return@withContext Result.failure()
        val appKey = inputData.getString("APP_KEY") ?: return@withContext Result.failure()

        try {
            Log.d("FlightWorker", "Running background sync...")

            val repository = getRepository()

            val today = LocalDate.now()
            val year = today.year
            val month = today.monthValue
            val day = today.dayOfMonth

            // Add all flights you want to track here
            val flights = listOf(
                Triple("IGO6107", "IGO", "6107"),
                Triple("AIC2963", "AIC", "2963"),
                Triple("AKJ1411", "AKJ", "1411")
            )

            for ((flightCode, carrier, flightNumber) in flights) {
                repository.fetchAndStoreFlightDuration(
                    flightCode = flightCode,
                    carrier = carrier,
                    flightNumber = flightNumber,
                    year = year,
                    month = month,
                    day = day,
                    appId = appId,
                    appKey = appKey
                )
            }

            scheduleNextRun(appContext)

            Result.success()
        } catch (e: Exception) {
            Log.e("FlightWorker", "Worker failed", e)
            Result.failure()
        }
    }

    private fun getRepository(): FlightDataRepository {
        val dao = FlightDatabase.getDatabase(appContext).flightDao()
        val api = FlightStatsApiService.api
        return FlightDataRepository(api, dao)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleNextRun(context: Context) {
        val delay = computeDelayUntilNext6PM()

        val workRequest = OneTimeWorkRequestBuilder<FlightSyncWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "FlightSyncWork",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )

        Log.d("FlightWorker", "Next sync scheduled after $delay ms")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun computeDelayUntilNext6PM(): Long {
        val now = LocalDateTime.now()
        val next6PM = if (now.hour >= 18) {
            now.toLocalDate().plusDays(1).atTime(18, 0)
        } else {
            now.toLocalDate().atTime(18, 0)
        }
        return Duration.between(now, next6PM).toMillis()
    }
}
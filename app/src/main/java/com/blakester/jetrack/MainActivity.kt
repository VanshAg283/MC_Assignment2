package com.blakester.jetrack

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.navigation.compose.rememberNavController
import com.blakester.jetrack.navigation.AppNavHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blakester.jetrack.data.database.Flight
import com.blakester.jetrack.data.database.FlightDatabase
import com.blakester.jetrack.data.network.FlightStatsApiService
import com.blakester.jetrack.data.repository.FlightDataRepository
import com.blakester.jetrack.ui.theme.JetrackTheme
import com.blakester.jetrack.ui.FlightTrackerScreen
import com.blakester.jetrack.viewmodel.FlightDataViewModel
import com.blakester.jetrack.viewmodel.FlightDataViewModelFactory
import com.mappls.sdk.maps.Mappls
import com.mappls.sdk.services.account.MapplsAccountManager
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.blakester.jetrack.workers.FlightSyncWorker
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        MapplsAccountManager.getInstance().restAPIKey = Keys.getRestApiKey(this)
        MapplsAccountManager.getInstance().mapSDKKey = Keys.getMapSdkKey(this)
        MapplsAccountManager.getInstance().atlasClientId = Keys.getClientId(this)
        MapplsAccountManager.getInstance().atlasClientSecret = Keys.getClientSecret(this)
        // ✅ Initialize Mappls SDK
        Mappls.getInstance(this)

        enableEdgeToEdge()
        setContent {
            JetrackTheme {
                val context = LocalContext.current
                val dao = FlightDatabase.getDatabase(context).flightDao()
                val api = FlightStatsApiService.api
                val repository = FlightDataRepository(api, dao)

                // ✅ Shared ViewModel created only once
                val viewModel: FlightDataViewModel = viewModel(
                    factory = FlightDataViewModelFactory(repository)
                )

                val appId = Keys.getFlightStatsAppId(context)
                val appKey = Keys.getFlightStatsAppKey(context)

                // ✅ Trigger preload only once
                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        viewModel.preloadDefaultFlights(appId, appKey)
                        scheduleInitialFlightWorker(context)
                    }
                }

                val navController = rememberNavController()
                AppNavHost(navController, flightDataViewModel = viewModel)
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun scheduleInitialFlightWorker(context: Context) {
    val now = LocalDateTime.now()
    val next6PM = if (now.hour >= 18) {
        now.toLocalDate().plusDays(1).atTime(18, 0)
    } else {
        now.toLocalDate().atTime(18, 0)
    }
    val delay = Duration.between(now, next6PM).toMillis()

    val appId = Keys.getFlightStatsAppId(context)
    val appKey = Keys.getFlightStatsAppKey(context)

    val inputData = workDataOf(
        "APP_ID" to appId,
        "APP_KEY" to appKey
    )

    val workRequest = OneTimeWorkRequestBuilder<FlightSyncWorker>()
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .setInputData(inputData)
        .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
        "FlightSyncWork",
        ExistingWorkPolicy.REPLACE,
        workRequest
    )

    Log.d("FlightWorker", "Next sync scheduled after a delay of $delay ms")
}



//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    JetrackTheme {
//        val navController = rememberNavController()
//        AppNavHost(navController)
//    }
//}
package com.blakester.jetrack.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blakester.jetrack.data.database.FlightDatabase
import com.blakester.jetrack.data.network.FlightStatsApiService
import com.blakester.jetrack.data.repository.FlightDataRepository
import com.blakester.jetrack.viewmodel.FlightDataViewModel
import com.blakester.jetrack.viewmodel.FlightDataViewModelFactory

@Composable
fun WeeklyDurationScreen(navController: NavController, viewModel: FlightDataViewModel) {
//    val context = LocalContext.current
//    val dao = FlightDatabase.getDatabase(context).flightDao()
//    val api = FlightStatsApiService.api
//    val repository = FlightDataRepository(api, dao)
//    val flightDataViewModel: FlightDataViewModel = viewModel(
//        factory = FlightDataViewModelFactory(repository)
//    )
    val flightDataViewModel = viewModel
    val averageDurations by flightDataViewModel.allAverages.observeAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
//        TextButton(
//            onClick = { navController.popBackStack() },
//            modifier = Modifier.align(Alignment.Start)
//        ) {
//            Text("⬅️")
//        }
//
//        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "📊 Weekly Flight Duration Insights",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(20.dp))

        when {
            averageDurations.isEmpty() -> {
                Text(
                    text = "No duration data available yet.\nPlease wait for the background job or preload it manually.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(averageDurations) { entry ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "✈️ Flight: ${entry.flightCode}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "📈 Avg Duration: ${"%.2f".format(entry.averageTime)} mins",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
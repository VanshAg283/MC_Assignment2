package com.blakester.jetrack.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalFocusManager
import com.blakester.jetrack.viewmodel.FlightViewModel
import androidx.navigation.NavController

@Composable
fun FlightTrackerScreen(navController: NavController) {
    val flightViewModel: FlightViewModel = viewModel()
    val state by flightViewModel.flightState.collectAsState()
    val context = LocalContext.current
    var input by remember { mutableStateOf("") }
    var isTracking by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // Stop tracking if error occurs
    LaunchedEffect(state.error) {
        if (state.error != null) {
            isTracking = false
            flightViewModel.stopTracking()  // <- this stops the coroutine in ViewModel
        }
    }

    // Dismiss keyboard & remove cursor when location is available
    LaunchedEffect(state.latitude, state.longitude) {
        if (state.latitude != null && state.longitude != null) {
            keyboardController?.hide()
            focusManager.clearFocus()
        }
    }

    LaunchedEffect(state.error) {
        if (state.error != null) {
            isTracking = false
            flightViewModel.stopTracking()

            // Delay and then clear error
            kotlinx.coroutines.delay(2000)
            flightViewModel.clearError()  // You’ll need to add this in your ViewModel
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Jetrack ✈️", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Enter Flight Number") },
            placeholder = { Text("e.g. AXB2664") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(
                onClick = {
                    isTracking = true
                    flightViewModel.fetchFlightLocationEveryMinute(input, context)
                },
                enabled = !isTracking
            ) {
                Text("Start Tracking")
            }

            Button(
                onClick = {
                    isTracking = false
                    flightViewModel.stopTracking()
                    flightViewModel.clearLocation()
                },
                enabled = isTracking
            ) {
                Text("Stop Tracking")
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        HorizontalDivider(thickness = 1.dp, color = Color.Gray.copy(alpha = 0.3f))

        Spacer(modifier = Modifier.height(16.dp))

        if (state.error != null) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = "❌ ${state.error}",
                    modifier = Modifier.padding(16.dp),
                    style = typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        } else if (state.latitude != null && state.longitude != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp) // Adjust as needed
            ) {
                // Main card (will blur when loading)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            alpha = if (state.loading) 0.4f else 1f
                        }
                        .blur(if (state.loading) 8.dp else 0.dp),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text("📍 Flight Details", style = typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        state.callsign?.let {
                            Text("✈️ Flight: $it")
                        }
                        state.origin_country?.let {
                            Text("🌍 Country: $it")
                        }
                        state.baro_altitude?.let {
                            Text("🛫 Barometric Altitude: ${"%.2f".format(it)} m")
                        }
                        state.velocity?.let {
                            val speedKmh = it * 3.6
                            Text("🚀 Speed: ${"%.2f".format(speedKmh)} km/hr")
                        }
                        state.true_track?.let {
                            Text("🧭 Direction: ${"%.2f".format(it)}°")
                        }
                        state.time_position?.let {
                            val timestamp = java.text.SimpleDateFormat("HH:mm:ss")
                                .format(java.util.Date(it * 1000L))
                            Text("⏱️ Last Position Update: $timestamp")
                        }
                        Text("🧭 Latitude: ${state.latitude}")
                        Text("🧭 Longitude: ${state.longitude}")
                        Spacer(modifier = Modifier.height(12.dp))
                        if (state.secondsUntilRefresh > 0) {
                            Text(
                                "🔄 Refresh in: ${state.secondsUntilRefresh} sec",
                                style = typography.bodySmall
                            )
                        }
                        OpenStreetMap(lat = state.latitude!!, lon = state.longitude!!)
                    }
                }

                // Overlay loader if refreshing
                if (state.loading) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Black.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(40.dp),
                            strokeWidth = 3.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        } else if (state.loading) {
            CircularProgressIndicator()
        } else if (!isTracking) {
            Text("Enter a flight number and press Start", style = typography.bodyMedium)
        }
    }
}
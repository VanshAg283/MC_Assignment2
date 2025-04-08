package com.blakester.jetrack.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.NavHost
import com.blakester.jetrack.ui.FlightTrackerScreen
import com.blakester.jetrack.ui.WeeklyDurationScreen
import com.blakester.jetrack.viewmodel.FlightDataViewModel

@Composable
fun AppNavHost(navController: NavHostController, flightDataViewModel: FlightDataViewModel) {
    val items = listOf(Screen.Tracker, Screen.Weekly)
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination == screen.route,
                        onClick = {
                            if (currentDestination != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Tracker.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Tracker.route) {
                FlightTrackerScreen(navController)
            }
            composable(Screen.Weekly.route) {
                WeeklyDurationScreen(navController, flightDataViewModel)
            }
        }
    }
}
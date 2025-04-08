package com.blakester.jetrack.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Tracker : Screen("tracker", "Tracker", Icons.Filled.Flight)
    data object Weekly : Screen("weekly", "Insights", Icons.Filled.QueryStats)
}
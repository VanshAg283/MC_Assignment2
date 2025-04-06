package com.blakester.jetrack

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.blakester.jetrack.ui.theme.JetrackTheme
import com.blakester.jetrack.ui.FlightTrackerScreen
import com.mappls.sdk.maps.Mappls
import com.mappls.sdk.services.account.MapplsAccountManager


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
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FlightTrackerScreen()
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JetrackTheme {
        FlightTrackerScreen()
    }
}
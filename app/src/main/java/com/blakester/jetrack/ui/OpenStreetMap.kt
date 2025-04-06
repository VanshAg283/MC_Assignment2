package com.blakester.jetrack.ui

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.mappls.sdk.maps.*
import com.mappls.sdk.maps.annotations.MarkerOptions
import com.mappls.sdk.maps.camera.CameraMapplsPinPosition
import com.mappls.sdk.maps.camera.CameraPosition
import com.mappls.sdk.maps.camera.CameraUpdateFactory
import com.mappls.sdk.maps.geometry.LatLng
import kotlinx.coroutines.*

@Composable
fun OpenStreetMap(lat: Double, lon: Double) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember {
        MapView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            onCreate(null)
            onStart()
            onResume()
        }
    }

    // 🧠 Keep track of previous position
    var previousLatLng by remember { mutableStateOf<LatLng?>(null) }
    var previousMarker by remember { mutableStateOf<com.mappls.sdk.maps.annotations.Marker?>(null) }

    val pathPoints = remember { mutableStateListOf<LatLng>() }
    var currentPolyline by remember { mutableStateOf<com.mappls.sdk.maps.annotations.Polyline?>(null) }
    var currentMarker by remember { mutableStateOf<com.mappls.sdk.maps.annotations.Marker?>(null) }

    DisposableEffect(lifecycleOwner) {
        val lifecycleObserver = object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                mapView.onStart()
            }

            override fun onResume(owner: LifecycleOwner) {
                mapView.onResume()
            }

            override fun onPause(owner: LifecycleOwner) {
                mapView.onPause()
            }

            override fun onStop(owner: LifecycleOwner) {
                mapView.onStop()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                mapView.onDestroy()
            }
        }

        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        factory = { mapView }
    ) { mv ->
        mv.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(mapplsMap: MapplsMap) {
                mapplsMap.uiSettings?.apply {
                    isRotateGesturesEnabled = false
                    isScrollGesturesEnabled = false
                    isTiltGesturesEnabled = false
                    isZoomGesturesEnabled = false
                }

                val currentLatLng = LatLng(lat, lon)

                if (pathPoints.isEmpty() || pathPoints.last() != currentLatLng) {
                    pathPoints.add(currentLatLng)

                    // Remove old marker
                    currentMarker?.let { mapplsMap.removeMarker(it) }

                    // Add marker at new location
                    currentMarker = mapplsMap.addMarker(
                        MarkerOptions()
                            .position(currentLatLng)
                            .title("Current Location")
                            .snippet("Lat: $lat, Lon: $lon")
                    )

                    // Remove old polyline
                    currentPolyline?.let { mapplsMap.removePolyline(it) }

                    // Draw updated polyline with full path
                    currentPolyline = mapplsMap.addPolyline(
                        com.mappls.sdk.maps.annotations.PolylineOptions()
                            .addAll(pathPoints)
                            .color(android.graphics.Color.parseColor("#3bb2d0"))
                            .width(2f)
                    )

                    // Move camera to new position
                    val cameraPosition = CameraPosition.Builder()
                        .target(currentLatLng)
                        .zoom(5.0)
                        .tilt(0.0)
                        .build()
                    mapplsMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                }

//                if (previousLatLng == null || previousLatLng != currentLatLng) {
//                    // Remove old ones
//                    previousMarker?.let { mapplsMap.removeMarker(it) }
//
//                    // Draw line from previous to current location (if previous exists)
//                    val polyline = previousLatLng?.let { prev ->
//                        mapplsMap.addPolyline(
//                            com.mappls.sdk.maps.annotations.PolylineOptions()
//                                .add(prev)
//                                .add(currentLatLng)
//                                .color(android.graphics.Color.BLUE)
//                                .width(5f)
//                        )
//                    }
//
//                    // Add new marker at current location
//                    val marker = mapplsMap.addMarker(
//                        MarkerOptions()
//                            .position(currentLatLng)
//                            .title("Current Location")
//                            .snippet("Lat: $lat, Lon: $lon")
//                    )
//
//                    // Animate camera
//                    val cameraPosition = CameraPosition.Builder()
//                        .target(currentLatLng)
//                        .zoom(5.0)
//                        .tilt(0.0)
//                        .build()
//                    mapplsMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
//
//                    // Save new references
//                    previousLatLng = currentLatLng
//                    previousMarker = marker
//                }
            }

            override fun onMapError(p0: Int, p1: String) {
                // Optional: Show a toast, log the error, or handle it gracefully
                println("Map Error [$p0]: $p1")
            }
        })
    }
}

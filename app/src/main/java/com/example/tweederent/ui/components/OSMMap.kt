package com.example.tweederent.ui.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.preference.PreferenceManager
import com.example.tweederent.data.Device
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun OSMMap(
    modifier: Modifier = Modifier,
    devices: List<Device>,
    onMarkerClick: (Device) -> Unit,
    initialPosition: GeoPoint = GeoPoint(51.2213, 4.4051),
    initialZoom: Double = 12.0,
    showUserLocation: Boolean = false
) {
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle(context)

    LaunchedEffect(mapView) {
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(initialZoom)
    }
    LaunchedEffect(initialPosition) {
        mapView.controller.animateTo(initialPosition, initialZoom, 1000L)
    }

    LaunchedEffect(devices) {
        mapView.overlays.clear()
        devices.forEach { device ->
            if (device.location.latitude != 0.0 && device.location.longitude != 0.0) {
                Marker(mapView).apply {
                    position = GeoPoint(device.location.latitude, device.location.longitude)
                    title = device.name
                    snippet = "€${device.dailyPrice}/day"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    setOnMarkerClickListener { _, _ ->
                        onMarkerClick(device)
                        true
                    }
                    mapView.overlays.add(this)
                }
            }
        }
        mapView.invalidate()
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier
    )
}

@Composable
private fun rememberMapViewWithLifecycle(context: Context): MapView {
    val mapView = remember {
        MapView(context).also { map ->
            Configuration.getInstance().load(
                context,
                PreferenceManager.getDefaultSharedPreferences(context)
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                mapView.onDetach()
                println("OSMMap: Successfully detached map")
            } catch (e: Exception) {
                println("OSMMap: Error detaching map: ${e.message}")
            }
        }
    }

    return mapView
}
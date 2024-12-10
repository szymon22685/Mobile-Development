package com.example.tweederent.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.preference.PreferenceManager
import com.example.tweederent.data.Location
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import android.view.MotionEvent

@Composable
fun LocationPicker(
    location: Location,
    onLocationSelected: (Location) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val mapView = remember {
        MapView(context).apply {
            Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(15.0)
        }
    }

    Column(modifier = modifier) {
        Text(
            text = "Tap on the map to select location",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            AndroidView(
                factory = { mapView },
                modifier = Modifier.fillMaxSize()
            ) { map ->
                // Set initial position (Antwerp center or existing location)
                val point = GeoPoint(
                    if (location.latitude != 0.0) location.latitude else 51.2213,
                    if (location.longitude != 0.0) location.longitude else 4.4051
                )

                map.controller.setCenter(point)

                // Clear previous overlays and add marker if location exists
                map.overlays.clear()
                if (location.latitude != 0.0 && location.longitude != 0.0) {
                    Marker(map).apply {
                        position = point
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        map.overlays.add(this)
                    }
                }

                // Handle map clicks
                map.overlays.add(object : Overlay(context) {
                    override fun onSingleTapConfirmed(e: MotionEvent, mapView: MapView): Boolean {
                        val projection = mapView.projection
                        val geoPoint = projection.fromPixels(e.x.toInt(), e.y.toInt()) as GeoPoint

                        onLocationSelected(
                            Location(
                                latitude = geoPoint.latitude,
                                longitude = geoPoint.longitude,
                                address = "Selected location",
                                city = "Antwerp",
                                postalCode = "2000"
                            )
                        )

                        // Update marker
                        map.overlays.clear()
                        Marker(mapView).apply {
                            position = geoPoint
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            map.overlays.add(this)
                        }
                        mapView.invalidate()
                        return true
                    }
                })
            }
        }
    }
}
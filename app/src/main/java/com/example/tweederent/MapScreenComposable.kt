package com.example.tweederent

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.preference.PreferenceManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tweederent.data.Device
import org.json.JSONArray
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


@Composable
fun MapScreen() {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var mapView by remember { mutableStateOf<MapView?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Zoekbalk
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Search location") },
                singleLine = true
            )

            Button(
                onClick = { performSearch(context, searchQuery, mapView) }
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        }

        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(16.0)
                    controller.setCenter(GeoPoint(51.2213, 4.4051))
                    mapView = this
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }

    SideEffect {
        Configuration.getInstance().apply {
            load(context, PreferenceManager.getDefaultSharedPreferences(context))
            userAgentValue = context.packageName
        }
    }
}
private fun performSearch(context: Context, query: String, mapView: MapView?) {
    if (query.isEmpty() || mapView == null) return

    val url = "https://nominatim.openstreetmap.org/search?format=json&q=${Uri.encode(query)}"
    val queue = Volley.newRequestQueue(context)

    val stringRequest = StringRequest(
        Request.Method.GET, url,
        { response ->
            try {
                val jsonArray = JSONArray(response)
                if (jsonArray.length() > 0) {
                    val location = jsonArray.getJSONObject(0)
                    val lat = location.getDouble("lat")
                    val lon = location.getDouble("lon")

                    // Move map to searched location
                    val searchPoint = GeoPoint(lat, lon)
                    mapView.controller.animateTo(searchPoint)
                    mapView.controller.setZoom(16.0)

                    // Add a marker
                    mapView.overlays.removeAll { it is Marker }
                    Marker(mapView).apply {
                        position = searchPoint
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = query
                        mapView.overlays.add(this)
                    }

                    mapView.invalidate()
                } else {
                    Toast.makeText(context, "Location not found", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error searching location", Toast.LENGTH_SHORT).show()
            }
        },
        {
            Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
        }
    )

    queue.add(stringRequest)
}
@Composable
fun ProfileScreen() {
    // Composable hergebruiken
    ProfilePage(
            userName = "John Doe", // Vervangen
            profilePictureResId = R.drawable.logo,
            availableDevices = getAvailableDevices(),
            rentedDevices = getRentedDevices()
    )
}

@Composable
fun ProfilePage(userName: String, profilePictureResId: Int, availableDevices: List<Device>, rentedDevices: List<Device>) {

}


private fun getAvailableDevices(): List<Device> = listOf(
        Device(
        name = "Power Drill",
        description = "Professional cordless drill",
        category = "Tools",
        dailyPrice = 15.99,
        securityDeposit = 50.00,
        condition = "Excellent"
)
)

private fun getRentedDevices(): List<Device> = listOf(
        Device(
        name = "Lawn Mower",
        description = "Self-propelled gas lawn mower",
        category = "Yard",
        dailyPrice = 29.99,
        securityDeposit = 80.00,
        condition = "Good"
)
)


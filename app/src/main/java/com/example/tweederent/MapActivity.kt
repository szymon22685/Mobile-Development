package com.example.tweederent

// build.gradle (Module)


// In your AndroidManifest.xml, add these permissions
// <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
// <uses-permission android:name="android.permission.INTERNET"/>
// <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
// <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


class MapActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var searchBar: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(
            applicationContext,
            getPreferences(MODE_PRIVATE)
        )

        setContentView(R.layout.activity_map)

        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)


        val startPoint = GeoPoint(51.2213, 4.4051)
        val mapController = mapView.controller
        mapController.setCenter(startPoint)
        mapController.setZoom(10.0)

        // Initialize SearchBar
        searchBar = findViewById(R.id.searchBar)
        setupSearchBar()
    }

    private fun setupSearchBar() {
        searchBar.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER &&
                        event.action == KeyEvent.ACTION_DOWN)) {
                performSearch(searchBar.text.toString())
                true
            } else false
        }
    }

    private fun performSearch(query: String) {
        val url = "https://nominatim.openstreetmap.org/search?format=json&q=${query}"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    val jsonArray = JSONArray(response)
                    if (jsonArray.length() > 0) {
                        val location = jsonArray.getJSONObject(0)
                        val lat = location.getDouble("lat")
                        val lon = location.getDouble("lon")

                        val searchPoint = GeoPoint(lat, lon)
                        mapView.controller.setCenter(searchPoint)
                        mapView.controller.setZoom(15.0)

                        val marker = Marker(mapView)
                        marker.position = searchPoint
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        marker.title = query
                        mapView.overlays.add(marker)
                        mapView.invalidate()
                    } else {
                        Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("SearchError", "Error parsing response", e)
                    Toast.makeText(this, "Error searching location", Toast.LENGTH_SHORT).show()
                }
            },
            {
                Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show()
            })

        queue.add(stringRequest)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
}
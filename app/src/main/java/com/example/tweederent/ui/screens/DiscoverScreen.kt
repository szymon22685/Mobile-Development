package com.example.tweederent.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.tweederent.data.Device
import com.example.tweederent.ui.components.OSMMap
import com.example.tweederent.ui.viewmodel.DiscoverViewModel
import com.example.tweederent.ui.viewmodel.ViewModelFactory
import org.osmdroid.util.GeoPoint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    onNavigate: (String) -> Unit = {},
    viewModel: DiscoverViewModel = viewModel(factory = ViewModelFactory())
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchActive by remember { mutableStateOf(false) }

    val devices by remember { viewModel.devices }
    val isLoading by remember { viewModel.isLoading }
    val error by remember { viewModel.error }

    LaunchedEffect(devices) {
        println("Devices updated: ${devices.size} items")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f)
            ) {
                OSMMap(
                    modifier = Modifier.fillMaxSize(),
                    devices = devices,
                    onMarkerClick = { /* TODO: open device wanneer er op een locatie wordt gedrukt */ },
                    initialPosition = GeoPoint(51.2213, 4.4051),
                    initialZoom = 12.0
                )

                SearchBar(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .zIndex(1f),
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = {
                        searchActive = false
                        viewModel.loadDevices(searchQuery)
                    },
                    active = searchActive,
                    onActiveChange = { searchActive = it },
                    leadingIcon = { Icon(Icons.Default.Search, "Search") },
                    placeholder = { Text("Search devices or locations") }
                ) {
                    // TODO: voeg zoekresultaten toe
                }
            }

            Surface(
                modifier = Modifier.weight(0.6f)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    when {
                        isLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        error != null -> {
                            Text(
                                text = error ?: "An error occurred",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(16.dp)
                            )
                        }
                        devices.isEmpty() -> {
                            Text(
                                text = "No devices found",
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(16.dp)
                            )
                        }
                        else -> {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(devices) { device ->
                                    DeviceCard(
                                        device = device,
                                        onClick = { onNavigate("device_detail/${device.id}") }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceCard(
    device: Device,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            AsyncImage(
                model = device.imageUrls.firstOrNull() ?: "/api/placeholder/400/300",
                contentDescription = device.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = device.name,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1
            )
            Text(
                text = "€${String.format("%.2f", device.dailyPrice)}/day",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            if (device.location.city.isNotEmpty()) {
                Text(
                    text = device.location.city,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
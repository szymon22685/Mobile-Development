package com.example.tweederent.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.tweederent.data.Device
import com.example.tweederent.navigation.Screen
import com.example.tweederent.ui.components.OSMMap
import com.example.tweederent.ui.viewmodel.DiscoverViewModel
import org.osmdroid.util.GeoPoint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    onNavigate: (String) -> Unit = {},
    viewModel: DiscoverViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchActive by remember { mutableStateOf(false) }
    val uiState by remember { viewModel.uiState }
    val selectedDevice by remember { viewModel.selectedDevice }
    val selectedCategory by remember { viewModel.selectedCategory }
    val categories = remember { listOf("All", "Tools", "Garden", "Kitchen", "Cleaning", "Party", "Sports") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Map Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f)
            ) {
                when (uiState) {
                    is DiscoverViewModel.UiState.Success -> {
                        val devices = (uiState as DiscoverViewModel.UiState.Success).devices
                        OSMMap(
                            modifier = Modifier.fillMaxSize(),
                            devices = devices,
                            onMarkerClick = { device ->
                                viewModel.selectDevice(device)
                                onNavigate(Screen.DeviceDetail.createRoute(device.id))
                            },
                            initialPosition = selectedDevice?.location?.let {
                                GeoPoint(it.latitude, it.longitude)
                            } ?: GeoPoint(51.2213, 4.4051)
                        )
                    }
                    else -> {
                        OSMMap(
                            modifier = Modifier.fillMaxSize(),
                            devices = emptyList(),
                            onMarkerClick = {},
                            initialPosition = GeoPoint(51.2213, 4.4051)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .zIndex(1f)
                ) {
                    // Search Bar
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = {
                            searchQuery = it
                            viewModel.searchDevices(it)
                        },
                        onSearch = {
                            searchActive = false
                        },
                        active = searchActive,
                        onActiveChange = { searchActive = it },
                        leadingIcon = { Icon(Icons.Default.Search, "Search") },
                        placeholder = { Text("Search devices or locations") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Empty search suggestions
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Category Filter
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { category ->
                            FilterChip(
                                selected = category == selectedCategory,
                                onClick = {
                                    viewModel.selectCategory(if (category == "All") null else category)
                                    viewModel.searchDevices(searchQuery)
                                },
                                label = { Text(category) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }

            // Device List Section
            Surface(
                modifier = Modifier.weight(0.6f)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    when (uiState) {
                        is DiscoverViewModel.UiState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        is DiscoverViewModel.UiState.Error -> {
                            Text(
                                text = (uiState as DiscoverViewModel.UiState.Error).message,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(16.dp)
                            )
                        }
                        is DiscoverViewModel.UiState.Success -> {
                            val devices = (uiState as DiscoverViewModel.UiState.Success).devices
                            if (devices.isEmpty()) {
                                Text(
                                    text = "No devices found",
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(16.dp)
                                )
                            } else {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    contentPadding = PaddingValues(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(devices, key = { it.id }) { device ->
                                        DeviceCard(
                                            device = device,
                                            onClick = {
                                                viewModel.selectDevice(device)
                                                onNavigate(Screen.DeviceDetail.createRoute(device.id))
                                            }
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeviceCard(
    device: Device,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            AsyncImage(
                model = device.imageUrls.firstOrNull() ?: "/api/placeholder/400/300",
                contentDescription = device.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
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
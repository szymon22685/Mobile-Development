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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import coil.compose.AsyncImage
import com.example.tweederent.data.Device
import com.example.tweederent.navigation.Screen
import com.example.tweederent.ui.components.OSMMap
import com.example.tweederent.ui.viewmodel.DiscoverViewModel
import org.osmdroid.util.GeoPoint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceCard(
    device: Device,
    onClick: () -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
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

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f)
            ) {
                when (uiState) {
                    is DiscoverViewModel.UiState.Success -> {
                        val devices = (uiState as DiscoverViewModel.UiState.Success).devices
                        val displayedDevices = selectedDevice?.let { selected ->
                            listOf(selected)
                        } ?: devices

                        OSMMap(
                            modifier = Modifier.fillMaxSize(),
                            devices = displayedDevices,
                            onMarkerClick = { device ->
                                if (selectedDevice?.id == device.id) {
                                    viewModel.selectDevice(null)
                                } else {
                                    viewModel.selectDevice(device)
                                }
                            },
                            initialPosition = selectedDevice?.location?.let {
                                GeoPoint(it.latitude, it.longitude)
                            } ?: GeoPoint(51.2213, 4.4051),
                            initialZoom = 15.0
                        )
                    }
                    else -> {
                        OSMMap(
                            modifier = Modifier.fillMaxSize(),
                            devices = emptyList(),
                            onMarkerClick = {},
                            initialPosition = GeoPoint(51.2213, 4.4051),
                            initialZoom = 15.0
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .zIndex(1f)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            viewModel.searchDevices(it)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        placeholder = { Text("Search devices or locations") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val categories = remember {
                            listOf(
                                "All" to null,
                                "Tools" to "cat_tools",
                                "Garden" to "cat_garden",
                                "Kitchen" to "cat_kitchen",
                                "Cleaning" to "cat_cleaning",
                                "Party" to "cat_party",
                                "Sports" to "cat_sports"
                            )
                        }

                        categories.forEach { (displayName, categoryId) ->
                            FilterChip(
                                selected = when (categoryId) {
                                    null -> selectedCategory == null
                                    else -> selectedCategory == categoryId
                                },
                                onClick = {
                                    viewModel.selectCategory(categoryId)
                                    viewModel.searchDevices(searchQuery)
                                },
                                label = { Text(displayName) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier.weight(0.6f)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    when (val state = uiState) {
                        is DiscoverViewModel.UiState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        is DiscoverViewModel.UiState.Error -> {
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(16.dp)
                            )
                        }
                        is DiscoverViewModel.UiState.Success -> {
                            val devices = state.devices
                            if (devices.isEmpty()) {
                                Text(
                                    text = "No devices found",
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(16.dp)
                                )
                            } else {
                                val gridState = rememberLazyGridState()

                                val sortedDevices = selectedDevice?.let { selected ->
                                    devices.sortedByDescending { it.id == selected.id }
                                } ?: devices

                                LaunchedEffect(selectedDevice) {
                                    if (selectedDevice != null) {
                                        gridState.animateScrollToItem(0)
                                    }
                                }

                                LazyVerticalGrid(
                                    state = gridState,
                                    columns = GridCells.Fixed(2),
                                    contentPadding = PaddingValues(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(sortedDevices, key = { it.id }) { device ->
                                        DeviceCard(
                                            device = device,
                                            onClick = {
                                                if (selectedDevice?.id == device.id) {
                                                    onNavigate(
                                                        Screen.DeviceDetail.createRoute(
                                                            device.id
                                                        )
                                                    )
                                                } else {
                                                    viewModel.selectDevice(device)
                                                }
                                            },
                                            isSelected = selectedDevice?.id == device.id
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
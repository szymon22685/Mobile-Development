package com.example.tweederent.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tweederent.ui.components.OSMMap
import com.example.tweederent.ui.viewmodel.DeviceDetailViewModel
import org.osmdroid.util.GeoPoint
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceDetailScreen(
    deviceId: String,
    viewModel: DeviceDetailViewModel = DeviceDetailViewModel(),
    onNavigateBack: () -> Unit
) {
    println("DeviceDetailScreen: Starting composition")
    val device by viewModel.device.collectAsState()
    val bookingState by viewModel.bookingState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(deviceId) {
        println("DeviceDetailScreen: LaunchedEffect triggered")
        if (device == null) {
            println("DeviceDetailScreen: Loading device because device is null")
            viewModel.loadDevice(deviceId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(device?.name ?: "Loading...") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                device == null -> {
                    println("DeviceDetailScreen: Showing loading state")
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    println("DeviceDetailScreen: Showing device content")
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Image carousel
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        ) {
                            AsyncImage(
                                model = device?.imageUrls?.firstOrNull() ?: "/api/placeholder/400/300",
                                contentDescription = "Device image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Image count indicator
                            device?.imageUrls?.let { urls ->
                                if (urls.size > 1) {
                                    Surface(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .align(Alignment.BottomEnd),
                                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                        shape = MaterialTheme.shapes.small
                                    ) {
                                        Text(
                                            "${urls.size} photos",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                }
                            }
                        }

                        // Device information section
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = device?.name ?: "",
                                style = MaterialTheme.typography.headlineMedium
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Price info
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Euro,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${NumberFormat.getCurrencyInstance(Locale.GERMANY).format(device?.dailyPrice)} per day",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Description
                            Text(
                                text = "About this device",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = device?.description ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            Divider(modifier = Modifier.padding(vertical = 16.dp))

                            // Location section
                            Text(
                                text = "Location",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            device?.location?.let { location ->
                                OSMMap(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    devices = listOf(device!!),
                                    onMarkerClick = {},
                                    initialPosition = GeoPoint(location.latitude, location.longitude),
                                    initialZoom = 15.0
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Booking section
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "Booking Details",
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Daily Rate",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Text(
                                                text = "${NumberFormat.getCurrencyInstance(Locale.GERMANY).format(device?.dailyPrice)}",
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }

                                        Column {
                                            Text(
                                                text = "Security Deposit",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Text(
                                                text = "${NumberFormat.getCurrencyInstance(Locale.GERMANY).format(device?.securityDeposit)}",
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = { showDatePicker = true },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.CalendarMonth, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Select Dates")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Show error if any
            AnimatedVisibility(
                visible = bookingState is DeviceDetailViewModel.BookingState.Error,
                modifier = Modifier.align(Alignment.Center)
            ) {
                if (bookingState is DeviceDetailViewModel.BookingState.Error) {
                    Text(
                        text = (bookingState as DeviceDetailViewModel.BookingState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }

    // Date picker dialog will be implemented in the next commit
    if (showDatePicker) {
        // TODO: Show date picker dialog
        showDatePicker = false
    }
}
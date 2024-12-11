package com.example.tweederent.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
    onNavigateBack: () -> Unit,
    viewModel: DeviceDetailViewModel = viewModel(factory = DeviceDetailViewModel.Factory())
) {
    val device by viewModel.device.collectAsState()
    val bookingState by viewModel.bookingState.collectAsState()
    val isOwner by viewModel.isOwner.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState(
        initialDisplayMode = DisplayMode.Input,
    )

    LaunchedEffect(deviceId) {
        if (device == null) {
            viewModel.loadDevice(deviceId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(device?.name ?: "Loading...") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
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
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        ) {
                            AsyncImage(
                                model = device?.imageUrls?.firstOrNull()
                                    ?: "/api/placeholder/400/300",
                                contentDescription = "Device image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

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
                                            modifier = Modifier.padding(
                                                horizontal = 8.dp,
                                                vertical = 4.dp
                                            ),
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                }
                            }
                        }

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
                                    text = "${
                                        NumberFormat.getCurrencyInstance(Locale.GERMANY)
                                            .format(device?.dailyPrice)
                                    } per day",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "About this device",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = device?.description ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Location",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .padding(vertical = 16.dp)
                            ) {
                                device?.location?.let { location ->
                                    OSMMap(
                                        modifier = Modifier.fillMaxSize(),
                                        devices = listOf(device!!),
                                        onMarkerClick = {},
                                        initialPosition = GeoPoint(
                                            location.latitude,
                                            location.longitude
                                        ),
                                        initialZoom = 15.0
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 6.dp
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
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
                                                text = NumberFormat.getCurrencyInstance(
                                                    Locale.GERMANY
                                                ).format(device?.dailyPrice),
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }

                                        Column {
                                            Text(
                                                text = "Security Deposit",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Text(
                                                text = NumberFormat.getCurrencyInstance(
                                                    Locale.GERMANY
                                                ).format(device?.securityDeposit),
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = {
                                            if (isOwner) {
                                                showDeleteConfirmDialog = true
                                            } else {
                                                showDatePicker = true
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isOwner)
                                                MaterialTheme.colorScheme.error
                                            else MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        if (isOwner) {
                                            Text("Delete Device")
                                        } else {
                                            Icon(Icons.Default.CalendarMonth, contentDescription = null)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Select Dates")
                                        }
                                    }
                                    if (showDeleteConfirmDialog) {
                                        AlertDialog(
                                            onDismissRequest = { showDeleteConfirmDialog = false },
                                            title = { Text("Delete Device") },
                                            text = { Text("Are you sure you want to delete this device? This action cannot be undone.") },
                                            confirmButton = {
                                                TextButton(
                                                    onClick = {
                                                        device?.id?.let { viewModel.deleteDevice(it) }
                                                        onNavigateBack()  // Just navigate back directly
                                                    }
                                                ) {
                                                    Text("Delete", color = MaterialTheme.colorScheme.error)
                                                }
                                            },
                                            dismissButton = {
                                                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                                                    Text("Cancel")
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

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

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        dateRangePickerState.selectedStartDateMillis?.let { startDate ->
                            dateRangePickerState.selectedEndDateMillis?.let { endDate ->
                                viewModel.createBooking(startDate, endDate)
                            }
                        }
                        showDatePicker = false
                    },
                    enabled = dateRangePickerState.selectedEndDateMillis != null
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                title = { Text("Select rental period") }
            )
        }
    }

    if (bookingState is DeviceDetailViewModel.BookingState.Success) {
        AlertDialog(
            onDismissRequest = {
                viewModel.resetBookingState()
                onNavigateBack()
            },
            title = { Text("Booking Confirmed") },
            text = { Text("Your booking request has been sent successfully!") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetBookingState()
                    onNavigateBack()
                }) {
                    Text("OK")
                }
            }
        )
    }
}
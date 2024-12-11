package com.example.tweederent.ui.screens

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.tweederent.data.Device
import com.example.tweederent.data.Rental
import com.example.tweederent.data.Review
import com.example.tweederent.ui.components.ReviewList
import com.example.tweederent.ui.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToDeviceDetail: (String) -> Unit,
    onNavigateToReview: (String) -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(
        "My Devices",
        "Device Requests", // Requests for devices I own
        "My Bookings",     // Rentals I've made
        "Reviews"
    )
    val uiState by remember { viewModel.uiState }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                actions = {
                    IconButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        onNavigateToLogin()
                    }) {
                        Icon(Icons.Default.ExitToApp, "Logout")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ProfileHeader()

            ScrollableTabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (uiState) {
                is ProfileViewModel.UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }

                is ProfileViewModel.UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = (uiState as ProfileViewModel.UiState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp)
                        )
                    }
                }

                is ProfileViewModel.UiState.Success -> {
                    val data = (uiState as ProfileViewModel.UiState.Success).data
                    when (selectedTab) {
                        0 -> DevicesList(
                            devices = data.devices,
                            onDeviceClick = onNavigateToDeviceDetail
                        )
                        1 -> RentalRequestsList(
                            requests = data.receivedRequests,
                            onApprove = { viewModel.approveRental(it) },
                            onDeny = { viewModel.denyRental(it) },
                            onDeviceClick = onNavigateToDeviceDetail
                        )
                        2 -> RentalsList(
                            rentals = data.myRentals,
                            onDeviceClick = onNavigateToDeviceDetail,
                            onReviewClick = onNavigateToReview
                        )
                        3 -> ReviewsList(
                            reviews = data.reviews,
                            uiState = uiState
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .size(100.dp)
                .padding(8.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            AsyncImage(
                model = FirebaseAuth.getInstance().currentUser?.photoUrl
                    ?: "/api/placeholder/100/100",
                contentDescription = "Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Text(
            text = FirebaseAuth.getInstance().currentUser?.displayName ?: "User",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Text(
            text = FirebaseAuth.getInstance().currentUser?.email ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DevicesList(
    devices: List<Device>,
    onDeviceClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (devices.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No devices listed yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(devices, key = { it.id }) { device ->
                Card(
                    onClick = { onDeviceClick(device.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = device.imageUrls.firstOrNull() ?: "/api/placeholder/100/100",
                            contentDescription = device.name,
                            modifier = Modifier.size(60.dp),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = device.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "€${String.format("%.2f", device.dailyPrice)}/day",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = "View Details"
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RentalsList(
    rentals: List<Rental>,
    onDeviceClick: (String) -> Unit,
    onReviewClick: (String) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    val (activeRentals, pastRentals) = rentals.partition {
        it.status == "ACTIVE"
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (activeRentals.isNotEmpty()) {
            item {
                Text(
                    text = "Active Rentals",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(activeRentals, key = { it.id }) { rental ->
                RentalCard(
                    rental = rental,
                    dateFormat = dateFormat,
                    onDeviceClick = onDeviceClick,
                    showReviewButton = false,
                    onReviewClick = {}
                )
            }
        }

        if (pastRentals.isNotEmpty()) {
            item {
                Text(
                    text = "Past Rentals",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(pastRentals, key = { it.id }) { rental ->
                RentalCard(
                    rental = rental,
                    dateFormat = dateFormat,
                    onDeviceClick = onDeviceClick,
                    showReviewButton = !rental.isReviewed && rental.status == "COMPLETED",
                    onReviewClick = onReviewClick
                )
            }
        }

        if (rentals.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No rentals found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
@Composable
private fun RentalRequestsList(
    requests: List<Rental>,
    onApprove: (String) -> Unit,
    onDeny: (String) -> Unit,
    onDeviceClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (requests.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No pending rental requests",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(requests) { rental ->
                RentalRequestCard(
                    rental = rental,
                    onApprove = { onApprove(rental.id) },
                    onDeny = { onDeny(rental.id) },
                    onDeviceClick = { onDeviceClick(rental.deviceId) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RentalRequestCard(
    rental: Rental,
    onApprove: () -> Unit,
    onDeny: () -> Unit,
    onDeviceClick: () -> Unit
) {
    Card(
        onClick = onDeviceClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Request #${rental.id.takeLast(6)}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "From: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(rental.startDate)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "To: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(rental.endDate)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Total: €${String.format("%.2f", rental.totalPrice)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDeny,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Deny")
                }

                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Approve")
                }
            }
        }
    }
}

@Composable
private fun ManageRentalsList(
    rentals: List<Rental>,
    onStartRental: (String) -> Unit,
    onCompleteRental: (String) -> Unit,
    onDeviceClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (rentals.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No active rentals",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(rentals) { rental ->
                ManageRentalCard(
                    rental = rental,
                    onStartRental = { onStartRental(rental.id) },
                    onCompleteRental = { onCompleteRental(rental.id) },
                    onDeviceClick = { onDeviceClick(rental.deviceId) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ManageRentalCard(
    rental: Rental,
    onStartRental: () -> Unit,
    onCompleteRental: () -> Unit,
    onDeviceClick: () -> Unit
) {
    Card(
        onClick = onDeviceClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Rental #${rental.id.takeLast(6)}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "From: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(rental.startDate)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "To: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(rental.endDate)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Status: ${rental.status}",
                style = MaterialTheme.typography.bodyMedium,
                color = when (rental.status) {
                    "APPROVED" -> MaterialTheme.colorScheme.tertiary
                    "ACTIVE" -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )

            if (rental.status == "APPROVED") {
                Button(
                    onClick = onStartRental,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Start Rental")
                }
            } else if (rental.status == "ACTIVE") {
                Button(
                    onClick = onCompleteRental,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Complete Rental")
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RentalCard(
    rental: Rental,
    dateFormat: SimpleDateFormat,
    onDeviceClick: (String) -> Unit,
    showReviewButton: Boolean,
    onReviewClick: (String) -> Unit
) {
    Card(
        onClick = { onDeviceClick(rental.deviceId) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Rental #${rental.id.takeLast(6)}",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "From: ${dateFormat.format(rental.startDate)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "To: ${dateFormat.format(rental.endDate)}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Status: ${rental.status}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = when (rental.status) {
                        "COMPLETED" -> MaterialTheme.colorScheme.primary
                        "ACTIVE" -> MaterialTheme.colorScheme.tertiary
                        "CANCELLED" -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }

            if (showReviewButton) {
                Button(
                    onClick = { onReviewClick(rental.id) },
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Leave Review")
                }
            }
        }
    }
}

@Composable
private fun ReviewsList(
    reviews: List<Review>,
    uiState: ProfileViewModel.UiState
) {
    when (uiState) {
        is ProfileViewModel.UiState.Success -> {
            if (reviews.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No reviews yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                ReviewList(
                    reviews = reviews,
                    reviewers = uiState.data.reviewers,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
        }
        is ProfileViewModel.UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is ProfileViewModel.UiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
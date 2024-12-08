package com.example.tweederent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.tooling.preview.Preview
import com.example.tweederent.data.Device

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ProfilePage(
                    userName = "John Doe", // Vervangen
                    profilePictureResId = R.drawable.logo, // Vervangen
                    availableDevices = getAvailableDevices(),
                    rentedDevices = getRentedDevices()
                )
            }
        }
    }

    // Items voor preview van de dropdowns
    private fun getAvailableDevices(): List<Device> = listOf(
        Device(
            name = "Power Drill",
            description = "Professional cordless drill",
            category = "Tools",
            dailyPrice = 15.99,
            securityDeposit = 50.00,
            condition = "Excellent"
        ),
        Device(
            name = "Camping Tent",
            description = "4-person waterproof tent",
            category = "Outdoor",
            dailyPrice = 25.50,
            securityDeposit = 75.00,
            condition = "Good"
        ),
        Device(
            name = "Pressure Washer",
            description = "High-powered electric pressure washer",
            category = "Tools",
            dailyPrice = 35.00,
            securityDeposit = 100.00,
            condition = "Like New"
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
        ),
        Device(
            name = "Portable Generator",
            description = "2000W quiet inverter generator",
            category = "Power",
            dailyPrice = 45.00,
            securityDeposit = 120.00,
            condition = "Excellent"
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(
    userName: String,
    profilePictureResId: Int,
    availableDevices: List<Device>,
    rentedDevices: List<Device>
) {
    // States van dropdowns
    var expandedAvailableItems by remember { mutableStateOf(false) }
    var expandedRentedItems by remember { mutableStateOf(false) }

    var selectedAvailableDevice by remember { mutableStateOf<Device?>(null) }
    var selectedRentedDevice by remember { mutableStateOf<Device?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profielfoto
        Image(
            painter = painterResource(id = profilePictureResId),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(150.dp)
                .padding(bottom = 16.dp),
            contentScale = ContentScale.Crop
        )

        // User Name
        Text(
            text = userName,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Beschikbare items in dropdown
        ExposedDropdownMenuBox(
            expanded = expandedAvailableItems,
            onExpandedChange = { expandedAvailableItems = !expandedAvailableItems },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally)

        ) {
            TextField(
                value = selectedAvailableDevice?.name ?: "Available Devices",
                onValueChange = {},
                readOnly = true,
                label = { Text("Available for Rent") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Dropdown"
                    )
                },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expandedAvailableItems,
                onDismissRequest = { expandedAvailableItems = false }
            ) {
                availableDevices.forEach { device ->
                    DropdownMenuItem(
                        text = {
                            Text("${device.name} - $${device.dailyPrice}/day")
                        },
                        onClick = {
                            selectedAvailableDevice = device
                            expandedAvailableItems = false
                        }
                    )
                }
            }
        }

        // Rented Devices Dropdown
        ExposedDropdownMenuBox(
            expanded = expandedRentedItems,
            onExpandedChange = { expandedRentedItems = !expandedRentedItems },
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = selectedRentedDevice?.name ?: "Rented Devices",
                onValueChange = {},
                readOnly = true,
                label = { Text("Currently Renting") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Dropdown"
                    )
                },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expandedRentedItems,
                onDismissRequest = { expandedRentedItems = false }
            ) {
                rentedDevices.forEach { device ->
                    DropdownMenuItem(
                        text = {
                            Text("${device.name} - $${device.dailyPrice}/day")
                        },
                        onClick = {
                            selectedRentedDevice = device
                            expandedRentedItems = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfilePagePreview() {
    ProfilePage(
        userName = "John Doe",
        profilePictureResId = android.R.drawable.ic_menu_myplaces,
        availableDevices = listOf(
            Device(
                name = "Power Drill",
                description = "Professional cordless drill",
                category = "Tools",
                dailyPrice = 15.99,
                securityDeposit = 50.00,
                condition = "Excellent"
            )
        ),
        rentedDevices = listOf(
            Device(
                name = "Lawn Mower",
                description = "Self-propelled gas lawn mower",
                category = "Yard",
                dailyPrice = 29.99,
                securityDeposit = 80.00,
                condition = "Good"
            )
        )
    )
}
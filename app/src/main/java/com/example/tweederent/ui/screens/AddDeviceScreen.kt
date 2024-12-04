// app/src/main/java/com/example/tweederent/ui/screens/AddDeviceScreen.kt
package com.example.tweederent.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tweederent.ui.components.ImagePicker
import com.example.tweederent.ui.components.LocationPicker
import com.example.tweederent.ui.viewmodel.DeviceViewModel

@Composable
fun AddDeviceScreen(
    viewModel: DeviceViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    // State collection
    val uiState by viewModel.uiState.collectAsState()
    val selectedImages by viewModel.selectedImages.collectAsState()

    // Image picker launcher
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        viewModel.addImages(uris)
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Simple header instead of TopAppBar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = "Add Device",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = viewModel.name,
                onValueChange = viewModel::updateName,
                label = { Text("Device Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.description,
                onValueChange = viewModel::updateDescription,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            OutlinedTextField(
                value = viewModel.category,
                onValueChange = viewModel::updateCategory,
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.dailyPrice,
                onValueChange = viewModel::updateDailyPrice,
                label = { Text("Daily Price (€)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.securityDeposit,
                onValueChange = viewModel::updateSecurityDeposit,
                label = { Text("Security Deposit (€)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.condition,
                onValueChange = viewModel::updateCondition,
                label = { Text("Condition") },
                modifier = Modifier.fillMaxWidth()
            )

            LocationPicker(
                location = viewModel.location,
                onLocationSelected = viewModel::updateLocation
            )

            ImagePicker(
                selectedImages = selectedImages,
                onAddImages = { imagePicker.launch("image/*") },
                onRemoveImage = viewModel::removeImage
            )

            Button(
                onClick = viewModel::submitDevice,
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is DeviceViewModel.UiState.Loading
            ) {
                Text("Add Device")
            }

            if (uiState is DeviceViewModel.UiState.Error) {
                Text(
                    text = (uiState as DeviceViewModel.UiState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
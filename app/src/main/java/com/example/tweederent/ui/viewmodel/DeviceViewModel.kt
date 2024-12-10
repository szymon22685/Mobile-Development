package com.example.tweederent.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tweederent.data.Device
import com.example.tweederent.data.Location
import com.example.tweederent.repository.DeviceRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeviceViewModel @Inject constructor(
    private val deviceRepository: DeviceRepository
) : ViewModel() {

    sealed class UiState {
        object Initial : UiState()
        object Loading : UiState()
        data class Success(val message: String) : UiState()
        data class Error(val message: String) : UiState()
    }

    var uiState by mutableStateOf<UiState>(UiState.Initial)
        private set

    private var _selectedImage = mutableStateOf<Uri?>(null)
    val selectedImage: Uri? get() = _selectedImage.value

    // Form fields
    var name by mutableStateOf("")
        private set
    var description by mutableStateOf("")
        private set
    var category by mutableStateOf("")
        private set
    var dailyPrice by mutableStateOf("")
        private set
    var securityDeposit by mutableStateOf("")
        private set
    var condition by mutableStateOf("Good")
        private set
    var location by mutableStateOf(Location())
        private set

    fun updateName(value: String) {
        name = value
    }

    fun updateDescription(value: String) {
        description = value
    }

    fun updateCategory(value: String) {
        category = value
    }

    fun updateDailyPrice(value: String) {
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d*$"))) {
            dailyPrice = value
        }
    }

    fun updateSecurityDeposit(value: String) {
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d*$"))) {
            securityDeposit = value
        }
    }

    fun updateCondition(value: String) {
        condition = value
    }

    fun updateLocation(newLocation: Location) {
        location = newLocation
    }

    fun updateSelectedImage(uri: Uri?) {
        _selectedImage.value = uri
    }

    fun submitDevice() {
        if (!validateInput()) {
            uiState = UiState.Error("Please fill in all required fields")
            return
        }

        viewModelScope.launch {
            uiState = UiState.Loading
            try {
                Log.d("DeviceViewModel", "Creating device with name: $name, category: $category")
                val device = Device(
                    name = name,
                    description = description,
                    category = category,
                    dailyPrice = dailyPrice.toDoubleOrNull() ?: 0.0,
                    securityDeposit = securityDeposit.toDoubleOrNull() ?: 0.0,
                    location = location,
                    condition = condition,
                    isAvailable = true
                )

                selectedImage?.let { uri ->
                    Log.d("DeviceViewModel", "Submitting device with image")
                    deviceRepository.addDevice(device, listOf(uri))
                        .onSuccess {
                            Log.d("DeviceViewModel", "Device added successfully")
                            uiState = UiState.Success("Device added successfully")
                            resetForm()
                        }
                        .onFailure {
                            Log.e("DeviceViewModel", "Failed to add device", it)
                            uiState = UiState.Error(it.message ?: "Failed to add device")
                        }
                } ?: run {
                    Log.d("DeviceViewModel", "No image selected")
                    uiState = UiState.Error("Please select an image")
                }
            } catch (e: Exception) {
                Log.e("DeviceViewModel", "Error submitting device", e)
                uiState = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    private fun validateInput(): Boolean {
        val isValid = name.isNotBlank() &&
                description.isNotBlank() &&
                category.isNotBlank() &&
                dailyPrice.isNotBlank() &&
                securityDeposit.isNotBlank() &&
                selectedImage != null &&
                location.latitude != 0.0 &&
                location.longitude != 0.0

        Log.d("DeviceViewModel", """
            Validation results:
            - Name: ${name.isNotBlank()}
            - Description: ${description.isNotBlank()}
            - Category: ${category.isNotBlank()}
            - Daily Price: ${dailyPrice.isNotBlank()}
            - Security Deposit: ${securityDeposit.isNotBlank()}
            - Image: ${selectedImage != null}
            - Location: ${location.latitude != 0.0 && location.longitude != 0.0}
            Overall valid: $isValid
        """.trimIndent())

        return isValid
    }

    private fun resetForm() {
        name = ""
        description = ""
        category = ""
        dailyPrice = ""
        securityDeposit = ""
        condition = "Good"
        location = Location()
        _selectedImage.value = null
    }
}
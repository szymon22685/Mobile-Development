// app/src/main/java/com/example/tweederent/ui/viewmodel/DeviceViewModel.kt
package com.example.tweederent.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tweederent.data.Device
import com.example.tweederent.data.Location
import com.example.tweederent.repository.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceViewModel @Inject constructor(
    private val deviceRepository: DeviceRepository
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        object Initial : UiState()
        data class Success(val message: String) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _selectedImages = MutableStateFlow<List<Uri>>(emptyList())
    val selectedImages: StateFlow<List<Uri>> = _selectedImages.asStateFlow()

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
    var condition by mutableStateOf("")
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

    fun addImages(uris: List<Uri>) {
        _selectedImages.value = _selectedImages.value + uris
    }

    fun removeImage(uri: Uri) {
        _selectedImages.value = _selectedImages.value - uri
    }

    fun submitDevice() {
        if (!validateInput()) {
            _uiState.value = UiState.Error("Please fill in all required fields")
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
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

                deviceRepository.addDevice(device, _selectedImages.value)
                    .onSuccess {
                        _uiState.value = UiState.Success("Device added successfully")
                        resetForm()
                    }
                    .onFailure {
                        _uiState.value = UiState.Error(it.message ?: "Failed to add device")
                    }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    private fun validateInput(): Boolean {
        return name.isNotBlank() &&
                description.isNotBlank() &&
                category.isNotBlank() &&
                dailyPrice.isNotBlank() &&
                securityDeposit.isNotBlank() &&
                _selectedImages.value.isNotEmpty()
    }

    private fun resetForm() {
        name = ""
        description = ""
        category = ""
        dailyPrice = ""
        securityDeposit = ""
        condition = ""
        location = Location()
        _selectedImages.value = emptyList()
    }
}
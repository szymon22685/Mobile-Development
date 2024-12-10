package com.example.tweederent.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tweederent.data.Device
import com.example.tweederent.repository.DeviceRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DiscoverViewModel : ViewModel() {
    private val TAG = "DiscoverViewModel"
    private val deviceRepository = DeviceRepository()
    private var searchJob: Job? = null

    sealed class UiState {
        object Loading : UiState()
        data class Success(val devices: List<Device>) : UiState()
        data class Error(val message: String) : UiState()
    }

    var uiState = mutableStateOf<UiState>(UiState.Loading)
        private set

    var selectedDevice = mutableStateOf<Device?>(null)
        private set

    fun loadDevices(query: String = "", category: String? = null) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            try {
                uiState.value = UiState.Loading
                // Add small delay to prevent too many requests while typing
                delay(300)
                deviceRepository.searchDevices(query, category)
                    .onSuccess { deviceList ->
                        Log.d(TAG, "Successfully loaded ${deviceList.size} devices")
                        uiState.value = UiState.Success(deviceList)
                    }
                    .onFailure { exception ->
                        Log.e(TAG, "Failed to load devices", exception)
                        uiState.value = UiState.Error(exception.message ?: "Failed to load devices")
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading devices", e)
                uiState.value = UiState.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }

    fun selectDevice(device: Device?) {
        selectedDevice.value = device
    }

    init {
        loadDevices()
    }
}
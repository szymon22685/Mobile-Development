package com.example.tweederent.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tweederent.data.Device
import com.example.tweederent.repository.DeviceRepository
import kotlinx.coroutines.launch

class DiscoverViewModel : ViewModel() {
    private val TAG = "DiscoverViewModel"
    private val deviceRepository = DeviceRepository()

    val devices = mutableStateOf<List<Device>>(emptyList())
    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)

    init {
        Log.d(TAG, "Initializing DiscoverViewModel")
        loadDevices()
    }

    fun loadDevices(query: String = "", category: String? = null) {
        Log.d(TAG, "Starting loadDevices, current devices count: ${devices.value.size}")
        viewModelScope.launch {
            try {
                Log.d(TAG, "Loading devices with query: $query, category: $category")
                isLoading.value = true
                error.value = null

                deviceRepository.searchDevices(query, category)
                    .onSuccess { deviceList ->
                        Log.d(TAG, "Successfully loaded ${deviceList.size} devices")
                        devices.value = deviceList
                    }
                    .onFailure { exception ->
                        Log.e(TAG, "Failed to load devices", exception)
                        error.value = exception.message ?: "Failed to load devices"
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading devices", e)
                error.value = e.message ?: "An unexpected error occurred"
            } finally {
                isLoading.value = false
            }
        }
    }
}
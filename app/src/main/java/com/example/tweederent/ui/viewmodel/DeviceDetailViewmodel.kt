package com.example.tweederent.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tweederent.data.Device
import com.example.tweederent.repository.DeviceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeviceDetailViewModel : ViewModel() {
    private val deviceRepository = DeviceRepository()

    private val _device = MutableStateFlow<Device?>(null)
    val device: StateFlow<Device?> = _device.asStateFlow()

    private val _bookingState = MutableStateFlow<BookingState>(BookingState.Initial)
    val bookingState: StateFlow<BookingState> = _bookingState.asStateFlow()

    private var isLoading = false

    fun loadDevice(deviceId: String) {
        if (isLoading) {
            println("DeviceDetailViewModel: Skip loading - already loading")
            return
        }

        println("DeviceDetailViewModel: Starting to load device: $deviceId")
        isLoading = true

        viewModelScope.launch {
            try {
                println("DeviceDetailViewModel: Calling repository.getDevice")
                deviceRepository.getDevice(deviceId)
                    .onSuccess { device ->
                        println("DeviceDetailViewModel: Successfully loaded device: ${device.name}")
                        _device.value = device
                    }
                    .onFailure { error ->
                        println("DeviceDetailViewModel: Failed to load device: ${error.message}")
                        _bookingState.value =
                            BookingState.Error("Failed to load device: ${error.message}")
                    }
            } catch (e: Exception) {
                println("DeviceDetailViewModel: Exception while loading device: ${e.message}")
                _bookingState.value = BookingState.Error(e.message ?: "An error occurred")
            } finally {
                isLoading = false
            }
        }
    }
    sealed class BookingState {
        object Initial : BookingState()
        object Loading : BookingState()
        data class Success(val message: String) : BookingState()
        data class Error(val message: String) : BookingState()
    }
}
package com.example.tweederent.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tweederent.data.Device
import com.example.tweederent.repository.DeviceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeviceDetailViewModel(
    private val deviceRepository: DeviceRepository = DeviceRepository()
) : ViewModel() {
    private val _device = MutableStateFlow<Device?>(null)
    val device: StateFlow<Device?> = _device.asStateFlow()

    private val _bookingState = MutableStateFlow<BookingState>(BookingState.Initial)
    val bookingState: StateFlow<BookingState> = _bookingState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadDevice(deviceId: String) {
        if (_isLoading.value) return

        viewModelScope.launch {
            try {
                _isLoading.value = true
                deviceRepository.getDevice(deviceId)
                    .onSuccess { device ->
                        _device.value = device
                        _bookingState.value = BookingState.Initial
                    }
                    .onFailure { error ->
                        _bookingState.value = BookingState.Error(error.message ?: "Failed to load device")
                    }
            } catch (e: Exception) {
                _bookingState.value = BookingState.Error(e.message ?: "An unexpected error occurred")
            } finally {
                _isLoading.value = false
            }
        }
    }

    sealed class BookingState {
        object Initial : BookingState()
        object Loading : BookingState()
        data class Success(val message: String) : BookingState()
        data class Error(val message: String) : BookingState()
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DeviceDetailViewModel::class.java)) {
                return DeviceDetailViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
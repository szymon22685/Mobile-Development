package com.example.tweederent.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tweederent.data.Device
import com.example.tweederent.repository.BookingRepository
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
    private val bookingRepository = BookingRepository()
    private val _isLoading = MutableStateFlow(false)

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

    fun resetBookingState() {
        _bookingState.value = BookingState.Initial
    }

    fun createBooking(startDate: Long, endDate: Long) {
        val device = _device.value ?: return

        viewModelScope.launch {
            _bookingState.value = BookingState.Loading

            try {
                bookingRepository.checkAvailability(device.id, startDate, endDate)
                    .onSuccess { isAvailable ->
                        if (!isAvailable) {
                            _bookingState.value = BookingState.Error("Device not available for selected dates")
                            return@launch
                        }

                        val days = ((endDate - startDate) / (1000 * 60 * 60 * 24)) + 1
                        val totalPrice = days * device.dailyPrice

                        bookingRepository.createBooking(
                            deviceId = device.id,
                            ownerId = device.ownerId,
                            startDate = startDate,
                            endDate = endDate,
                            totalPrice = totalPrice
                        ).onSuccess {
                            _bookingState.value = BookingState.Success
                        }.onFailure { e ->
                            _bookingState.value = BookingState.Error(e.message ?: "Failed to create booking")
                        }
                    }.onFailure { e ->
                        _bookingState.value = BookingState.Error(e.message ?: "Failed to check availability")
                    }
            } catch (e: Exception) {
                _bookingState.value = BookingState.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }

    sealed class BookingState {
        object Initial : BookingState()
        object Loading : BookingState()
        object Success : BookingState()
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
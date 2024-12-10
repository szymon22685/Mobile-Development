package com.example.tweederent.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tweederent.data.Device
import com.example.tweederent.data.Rental
import com.example.tweederent.data.Review
import com.example.tweederent.repository.DeviceRepository
import com.example.tweederent.repository.RentalRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val deviceRepository = DeviceRepository()
    private val rentalRepository = RentalRepository()
    private val auth = FirebaseAuth.getInstance()

    data class ProfileData(
        val devices: List<Device> = emptyList(),
        val rentals: List<Rental> = emptyList(),
        val reviews: List<Review> = emptyList()
    )

    sealed class UiState {
        object Loading : UiState()
        data class Success(val data: ProfileData) : UiState()
        data class Error(val message: String) : UiState()
    }

    var uiState = mutableStateOf<UiState>(UiState.Loading)
        private set

    init {
        loadProfileData()
    }

    fun loadProfileData() {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                uiState.value = UiState.Loading

                val devicesResult = deviceRepository.getDevicesByOwner(userId)
                val devices = devicesResult.getOrNull() ?: emptyList()

                val rentalsResult = rentalRepository.getUserRentals(userId)
                val rentals = rentalsResult.getOrNull() ?: emptyList()

                val reviews = emptyList<Review>()

                uiState.value = UiState.Success(
                    ProfileData(
                        devices = devices,
                        rentals = rentals,
                        reviews = reviews
                    )
                )
            } catch (e: Exception) {
                uiState.value = UiState.Error(e.message ?: "Failed to load profile data")
            }
        }
    }

    fun refreshData() {
        loadProfileData()
    }
}
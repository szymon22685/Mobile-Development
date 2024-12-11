package com.example.tweederent.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tweederent.data.Device
import com.example.tweederent.data.Rental
import com.example.tweederent.data.Review
import com.example.tweederent.data.model.User
import com.example.tweederent.repository.DeviceRepository
import com.example.tweederent.repository.RentalRepository
import com.example.tweederent.repository.ReviewRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {
    private val deviceRepository = DeviceRepository()
    private val rentalRepository = RentalRepository()
    private val reviewRepository = ReviewRepository()
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    data class ProfileData(
        val devices: List<Device> = emptyList(),
        val receivedRequests: List<Rental> = emptyList(),
        val activeRentals: List<Rental> = emptyList(),
        val myRentals: List<Rental> = emptyList(),
        val reviews: List<Review> = emptyList(),
        val reviewers: Map<String, User> = emptyMap()
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

                val receivedRequestsResult = rentalRepository.getReceivedRentalRequests(userId)
                val receivedRequests = receivedRequestsResult.getOrNull() ?: emptyList()

                val activeRentalsResult = rentalRepository.getActiveRentals(userId)
                val activeRentals = activeRentalsResult.getOrNull() ?: emptyList()

                val myRentalsResult = rentalRepository.getUserRentals(userId)
                val myRentals = myRentalsResult.getOrNull() ?: emptyList()

                val reviewsResult = reviewRepository.getUserReviews(userId)
                val reviews = reviewsResult.getOrNull() ?: emptyList()

                // Fetch reviewer details
                val reviewerIds = reviews.map { it.reviewerId }.distinct()
                val reviewers = reviewerIds.mapNotNull { reviewerId ->
                    try {
                        val userDoc = db.collection("users").document(reviewerId).get().await()
                        val user = userDoc.toObject(User::class.java)
                        if (user != null) reviewerId to user else null
                    } catch (e: Exception) {
                        null
                    }
                }.toMap()

                uiState.value = UiState.Success(
                    ProfileData(
                        devices = devices,
                        receivedRequests = receivedRequests,
                        activeRentals = activeRentals,
                        myRentals = myRentals,
                        reviews = reviews,
                        reviewers = reviewers
                    )
                )
            } catch (e: Exception) {
                uiState.value = UiState.Error(e.message ?: "Failed to load profile data")
            }
        }
    }

    fun approveRental(rentalId: String) {
        viewModelScope.launch {
            try {
                rentalRepository.approveRental(rentalId)
                loadProfileData()
            } catch (e: Exception) {
            }
        }
    }

    fun denyRental(rentalId: String) {
        viewModelScope.launch {
            try {
                rentalRepository.denyRental(rentalId)
                loadProfileData()
            } catch (e: Exception) {
            }
        }
    }

    fun startRental(rentalId: String) {
        viewModelScope.launch {
            try {
                rentalRepository.startRental(rentalId)
                loadProfileData()
            } catch (e: Exception) {
            }
        }
    }

    fun completeRental(rentalId: String) {
        viewModelScope.launch {
            try {
                rentalRepository.completeRental(rentalId)
                loadProfileData()
            } catch (e: Exception) {
            }
        }
    }

    fun refreshData() {
        loadProfileData()
    }
}
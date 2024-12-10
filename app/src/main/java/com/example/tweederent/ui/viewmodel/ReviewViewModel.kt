package com.example.tweederent.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tweederent.data.Rental
import com.example.tweederent.data.Review
import com.example.tweederent.repository.RentalRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ReviewViewModel : ViewModel() {
    private val rentalRepository = RentalRepository()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    sealed class UiState {
        object Loading : UiState()
        data class Success(val rental: Rental) : UiState()
        data class Error(val message: String) : UiState()
    }

    var uiState = mutableStateOf<UiState>(UiState.Loading)
        private set

    private var currentRental: Rental? = null
    private var navigationCallback: (() -> Unit)? = null

    fun setNavigationCallback(callback: () -> Unit) {
        navigationCallback = callback
    }

    fun navigateBack() {
        navigationCallback?.invoke()
    }

    fun loadRental(rentalId: String) {
        viewModelScope.launch {
            try {
                val rentalDoc = db.collection("rentals").document(rentalId).get().await()
                val rental = rentalDoc.toObject(Rental::class.java)

                if (rental != null) {
                    currentRental = rental
                    uiState.value = UiState.Success(rental)
                } else {
                    uiState.value = UiState.Error("Rental not found")
                }
            } catch (e: Exception) {
                uiState.value = UiState.Error(e.message ?: "Failed to load rental")
            }
        }
    }

    fun submitReview(rating: Int, comment: String) {
        val rental = currentRental ?: return
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                uiState.value = UiState.Loading

                val review = Review(
                    id = UUID.randomUUID().toString(),
                    rentalId = rental.id,
                    reviewerId = userId,
                    reviewedId = rental.ownerId,
                    deviceId = rental.deviceId,
                    rating = rating,
                    comment = comment,
                    createDate = System.currentTimeMillis()
                )

                db.collection("reviews").document(review.id).set(review).await()

                rentalRepository.markRentalAsReviewed(rental.id)

                navigationCallback?.invoke()
            } catch (e: Exception) {
                uiState.value = UiState.Error(e.message ?: "Failed to submit review")
            }
        }
    }
}
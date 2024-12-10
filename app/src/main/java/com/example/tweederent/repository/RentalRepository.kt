package com.example.tweederent.repository

import com.example.tweederent.data.Rental
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RentalRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val rentalsCollection = db.collection("rentals")

    suspend fun getUserRentals(userId: String? = null): Result<List<Rental>> {
        return try {
            val currentUserId = userId ?: auth.currentUser?.uid ?:
            return Result.failure(IllegalStateException("No user logged in"))

            val rentals = rentalsCollection
                .whereEqualTo("renterId", currentUserId)
                .get()
                .await()
                .toObjects(Rental::class.java)

            Result.success(rentals.sortedByDescending { it.createDate })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOwnedDeviceRentals(userId: String? = null): Result<List<Rental>> {
        return try {
            val currentUserId = userId ?: auth.currentUser?.uid ?:
            return Result.failure(IllegalStateException("No user logged in"))

            val rentals = rentalsCollection
                .whereEqualTo("ownerId", currentUserId)
                .get()
                .await()
                .toObjects(Rental::class.java)

            Result.success(rentals.sortedByDescending { it.createDate })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createRental(rental: Rental): Result<String> = try {
        val rentalId = rentalsCollection.document().id
        val newRental = rental.copy(
            id = rentalId,
            createDate = System.currentTimeMillis(),
            status = "PENDING"
        )

        rentalsCollection.document(rentalId).set(newRental).await()
        Result.success(rentalId)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateRentalStatus(rentalId: String, status: String): Result<Unit> = try {
        rentalsCollection.document(rentalId)
            .update("status", status)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun markRentalAsReviewed(rentalId: String): Result<Unit> = try {
        rentalsCollection.document(rentalId)
            .update("isReviewed", true)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
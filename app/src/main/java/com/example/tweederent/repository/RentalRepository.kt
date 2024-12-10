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

    suspend fun getReceivedRentalRequests(userId: String? = null): Result<List<Rental>> {
        return try {
            val currentUserId = userId ?: auth.currentUser?.uid ?:
            return Result.failure(IllegalStateException("No user logged in"))

            val rentals = rentalsCollection
                .whereEqualTo("ownerId", currentUserId)
                .whereEqualTo("status", "PENDING")
                .get()
                .await()
                .toObjects(Rental::class.java)

            Result.success(rentals.sortedByDescending { it.createDate })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getActiveRentals(userId: String? = null): Result<List<Rental>> {
        return try {
            val currentUserId = userId ?: auth.currentUser?.uid ?:
            return Result.failure(IllegalStateException("No user logged in"))

            val rentals = rentalsCollection
                .whereEqualTo("ownerId", currentUserId)
                .whereIn("status", listOf("APPROVED", "ACTIVE"))
                .get()
                .await()
                .toObjects(Rental::class.java)

            Result.success(rentals.sortedByDescending { it.createDate })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun approveRental(rentalId: String): Result<Unit> = try {
        rentalsCollection.document(rentalId)
            .update(
                mapOf(
                    "status" to "APPROVED",
                    "updateDate" to System.currentTimeMillis()
                )
            )
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun denyRental(rentalId: String): Result<Unit> = try {
        rentalsCollection.document(rentalId)
            .update(
                mapOf(
                    "status" to "CANCELLED",
                    "updateDate" to System.currentTimeMillis()
                )
            )
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun startRental(rentalId: String): Result<Unit> = try {
        rentalsCollection.document(rentalId)
            .update(
                mapOf(
                    "status" to "ACTIVE",
                    "updateDate" to System.currentTimeMillis()
                )
            )
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun completeRental(rentalId: String): Result<Unit> = try {
        rentalsCollection.document(rentalId)
            .update(
                mapOf(
                    "status" to "COMPLETED",
                    "updateDate" to System.currentTimeMillis()
                )
            )
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun markRentalAsReviewed(rentalId: String): Result<Unit> = try {
        rentalsCollection.document(rentalId)
            .update(
                mapOf(
                    "isReviewed" to true,
                    "updateDate" to System.currentTimeMillis()
                )
            )
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
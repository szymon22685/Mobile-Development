package com.example.tweederent.repository

import com.example.tweederent.data.Rental
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class BookingRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val rentalsCollection = db.collection("rentals")

    suspend fun createBooking(
        deviceId: String,
        ownerId: String,
        startDate: Long,
        endDate: Long,
        totalPrice: Double
    ): Result<String> = try {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

        val rental = Rental(
            deviceId = deviceId,
            renterId = userId,
            ownerId = ownerId,
            startDate = startDate,
            endDate = endDate,
            totalPrice = totalPrice,
            status = "PENDING",
            createDate = System.currentTimeMillis()
        )

        val docRef = rentalsCollection.document()
        val rentalWithId = rental.copy(id = docRef.id)

        docRef.set(rentalWithId).await()
        Result.success(docRef.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun checkAvailability(deviceId: String, startDate: Long, endDate: Long): Result<Boolean> = try {
        val rentals = rentalsCollection
            .whereEqualTo("deviceId", deviceId)
            .get()
            .await()

        val hasOverlap = rentals.any { rental ->
            val rentalData = rental.toObject(Rental::class.java)
            rentalData.endDate >= startDate && rentalData.startDate <= endDate
        }

        Result.success(!hasOverlap)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
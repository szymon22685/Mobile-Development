package com.example.tweederent.repository

import com.example.tweederent.data.Review
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ReviewRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val reviewsCollection = db.collection("reviews")

    suspend fun createReview(
        deviceId: String,
        reviewedId: String,
        rating: Int,
        comment: String
    ): Result<String> = try {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

        val review = Review(
            id = UUID.randomUUID().toString(),
            deviceId = deviceId,
            reviewerId = userId,
            reviewedId = reviewedId,
            rating = rating,
            comment = comment,
            createDate = System.currentTimeMillis()
        )

        reviewsCollection.document(review.id).set(review).await()
        Result.success(review.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getUserReviews(userId: String): Result<List<Review>> = try {
        val reviews = reviewsCollection
            .whereEqualTo("reviewedId", userId)
            .get()
            .await()
            .toObjects(Review::class.java)
        Result.success(reviews.sortedByDescending { it.createDate })
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getDeviceReviews(deviceId: String): Result<List<Review>> = try {
        val reviews = reviewsCollection
            .whereEqualTo("deviceId", deviceId)
            .get()
            .await()
            .toObjects(Review::class.java)
        Result.success(reviews.sortedByDescending { it.createDate })
    } catch (e: Exception) {
        Result.failure(e)
    }
}
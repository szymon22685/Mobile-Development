package com.example.tweederent.data

import com.google.firebase.firestore.PropertyName

data class Rental(
    val id: String = "",
    val deviceId: String = "",
    val renterId: String = "",
    val ownerId: String = "",
    @get:PropertyName("startDate")
    @set:PropertyName("startDate")
    var startDate: Long = 0,
    @get:PropertyName("endDate")
    @set:PropertyName("endDate")
    var endDate: Long = 0,
    val totalPrice: Double = 0.0,
    val status: String = "PENDING",
    @get:PropertyName("isReviewed")
    @set:PropertyName("isReviewed")
    var isReviewed: Boolean = false,
    @get:PropertyName("createDate")
    @set:PropertyName("createDate")
    var createDate: Long = System.currentTimeMillis()
)
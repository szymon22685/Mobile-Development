package com.example.tweederent.data

import com.google.firebase.firestore.PropertyName

data class Review(
    val id: String = "",
    val rentalId: String = "",
    val reviewerId: String = "",
    val reviewedId: String = "",
    val deviceId: String = "",
    val rating: Int = 0,
    val comment: String = "",
    @get:PropertyName("createDate")
    @set:PropertyName("createDate")
    var createDate: Long = System.currentTimeMillis()
)
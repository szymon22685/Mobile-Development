package com.example.tweederent.data.model

import com.example.tweederent.data.Location
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val profileImageUrl: String = "",
    val location: Location = Location(),
    val rating: Double = 0.0,
    @get:PropertyName("reviewCount")
    @set:PropertyName("reviewCount")
    var reviewCount: Int = 0,
    @get:PropertyName("createDate")
    @set:PropertyName("createDate")
    var createDate: Long = System.currentTimeMillis(),
    @get:Exclude var ownedDevices: List<String> = emptyList(),
    @get:Exclude var activeRentals: List<String> = emptyList()
)
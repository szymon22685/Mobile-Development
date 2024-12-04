package com.example.tweederent.data

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

data class Device(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val ownerId: String = "",
    @get:PropertyName("dailyPrice")
    @set:PropertyName("dailyPrice")
    var dailyPrice: Double = 0.0,
    @get:PropertyName("securityDeposit")
    @set:PropertyName("securityDeposit")
    var securityDeposit: Double = 0.0,
    val location: Location = Location(),
    val imageUrls: List<String> = emptyList(),
    val condition: String = "",
    @get:PropertyName("isAvailable")
    @set:PropertyName("isAvailable")
    var isAvailable: Boolean = true,
    @get:PropertyName("lastMaintenanceDate")
    @set:PropertyName("lastMaintenanceDate")
    var lastMaintenanceDate: Long? = null,
    @get:PropertyName("createDate")
    @set:PropertyName("createDate")
    var createDate: Long = System.currentTimeMillis(),
    @get:Exclude var bookedDates: List<BookedPeriod> = emptyList()
)
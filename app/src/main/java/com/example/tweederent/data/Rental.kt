package com.example.tweederent.data

import com.google.firebase.firestore.PropertyName
import com.example.tweederent.data.enums.RentalStatus

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
    val status: RentalStatus = RentalStatus.PENDING,
    @get:PropertyName("totalPrice")
    @set:PropertyName("totalPrice")
    var totalPrice: Double = 0.0,
    @get:PropertyName("securityDeposit")
    @set:PropertyName("securityDeposit")
    var securityDeposit: Double = 0.0,
    @get:PropertyName("createDate")
    @set:PropertyName("createDate")
    var createDate: Long = System.currentTimeMillis()
)
package com.example.tweederent.data

data class RentalItem(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val ownerId: String = "",
    val borrowerId: String? = null,
    val isAvailable: Boolean = true,
    val category: String = "",
    val imageUrl: String? = null
)
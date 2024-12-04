package com.example.tweederent.data

data class Location(
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val city: String = "",
    val postalCode: String = ""
)
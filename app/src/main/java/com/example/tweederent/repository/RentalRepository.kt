package com.example.tweederent.repository;

import com.example.tweederent.data.Device
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RentalRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val rentalsCollection = db.collection("rentals")

    fun getCurrentUserRentals(callback: (List<Device>) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        rentalsCollection
            .whereEqualTo("ownerId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val rentals = documents.map { it.toObject(Device::class.java) }
                callback(rentals)
            }
    }

    fun getBorrowedItems(callback: (List<Device>) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        rentalsCollection
            .whereEqualTo("borrowerId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val rentals = documents.map { it.toObject(Device::class.java) }
                callback(rentals)
            }
    }
}
package com.example.tweederent.repository;

import com.example.tweederent.data.RentalItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RentalRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val rentalsCollection = db.collection("rentals")

    fun getCurrentUserRentals(callback: (List<RentalItem>) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        rentalsCollection
            .whereEqualTo("ownerId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val rentals = documents.map { it.toObject(RentalItem::class.java) }
                callback(rentals)
            }
    }

    fun getBorrowedItems(callback: (List<RentalItem>) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        rentalsCollection
            .whereEqualTo("borrowerId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val rentals = documents.map { it.toObject(RentalItem::class.java) }
                callback(rentals)
            }
    }
}
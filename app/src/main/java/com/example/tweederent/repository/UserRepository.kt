package com.example.tweederent.repository;

import com.example.tweederent.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    fun getCurrentUser(callback: (User?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return callback(null)
        usersCollection.document(userId).get()
                .addOnSuccessListener { document ->
                callback(document.toObject(User::class.java))
        }
    }

    fun updateUser(user: User, callback: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return callback(false)
        usersCollection.document(userId)
                .set(user)
                .addOnCompleteListener { task ->
                callback(task.isSuccessful)
        }
    }
}
package com.example.tweederent.repository;

import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    fun getCurrentUser(@SuppressLint("RestrictedApi") callback: (User?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return callback(null)
        usersCollection.document(userId).get()
                .addOnSuccessListener { document ->
                callback(document.toObject(User::class.java))
        }
    }

    fun updateUser(@SuppressLint("RestrictedApi") user: User, callback: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return callback(false)
        usersCollection.document(userId)
                .set(user)
                .addOnCompleteListener { task ->
                callback(task.isSuccessful)
        }
    }
}
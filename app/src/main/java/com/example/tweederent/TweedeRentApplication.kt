package com.example.tweederent

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class TweedeRentApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            FirebaseApp.initializeApp(this)
            FirebaseAuth.getInstance().signOut() // enkel voor development
            Log.d("TweedeRentApplication", "Firebase initialized successfully")
        } catch (e: Exception) {
            Log.e("TweedeRentApplication", "Failed to initialize Firebase", e)
        }
    }
}
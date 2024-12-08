package com.example.tweederent

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class TweedeRentApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            FirebaseApp.initializeApp(this)
            Log.d("TweedeRentApplication", "Firebase initialized successfully")
        } catch (e: Exception) {
            Log.e("TweedeRentApplication", "Failed to initialize Firebase", e)
        }
    }
}
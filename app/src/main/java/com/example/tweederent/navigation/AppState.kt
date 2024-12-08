package com.example.tweederent.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth

@Composable
fun rememberAppState(): AppState {
    return remember { AppState() }
}

class AppState {
    var isAuthenticated by mutableStateOf(false)
        private set

    private val auth = FirebaseAuth.getInstance()

    init {
        updateAuthState()
        auth.addAuthStateListener { updateAuthState() }
    }

    private fun updateAuthState() {
        isAuthenticated = auth.currentUser != null
    }
}
package com.example.tweederent

import AddDeviceScreen
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.tweederent.ui.screens.DiscoverScreen
import com.example.tweederent.ui.screens.LoginScreen
import com.example.tweederent.ui.screens.ProfileScreen
import com.example.tweederent.ui.screens.RegisterScreen
import com.example.tweederent.ui.theme.TweedeRentTheme
import com.example.tweederent.utils.DataSeeder
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            try {
                DataSeeder().seedData()
                Log.d("MainActivity", "Data seeding completed")
            } catch (e: Exception) {
                Log.e("MainActivity", "Error seeding data", e)
            }
        }

        setContent {
            TweedeRentTheme {
                var selectedTab by remember { mutableStateOf(0) }

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Home, "Discover") },
                                label = { Text("Discover") },
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Add, "Add") },
                                label = { Text("Add") },
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Person, "Profile") },
                                label = { Text("Profile") },
                                selected = selectedTab == 2,
                                onClick = { selectedTab = 2 }
                            )
                        }
                    }
                ) { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        when (selectedTab) {
                            0 -> DiscoverScreen()
                            1 -> AddDeviceScreen()
                            2 -> ProfileScreen(
                                onNavigateToLogin = {
                                    selectedTab = 3
                                },
                                onNavigateToDeviceDetail = { /* TODO: herleid naar device detail pagina */ }
                            )
                            3 -> LoginScreen(
                                onLoginSuccess = {
                                    selectedTab = 0
                                },
                                onNavigateToRegister = {
                                    selectedTab = 4
                                }
                            )
                            4 -> RegisterScreen(
                                onRegisterSuccess = {
                                    selectedTab = 0
                                },
                                onNavigateToLogin = {
                                    selectedTab = 3
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
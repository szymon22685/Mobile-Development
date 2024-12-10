package com.example.tweederent

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tweederent.navigation.AppNavigation
import com.example.tweederent.navigation.Screen
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
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    bottomBar = {
                        if (currentRoute in listOf(Screen.Discover.route, Screen.AddDevice.route, Screen.Profile.route)) {
                            NavigationBar {
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Home, "Discover") },
                                    label = { Text("Discover") },
                                    selected = currentRoute == Screen.Discover.route,
                                    onClick = {
                                        navController.navigate(Screen.Discover.route) {
                                            popUpTo(Screen.Discover.route) { inclusive = true }
                                        }
                                    }
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Add, "Add") },
                                    label = { Text("Add") },
                                    selected = currentRoute == Screen.AddDevice.route,
                                    onClick = {
                                        navController.navigate(Screen.AddDevice.route) {
                                            popUpTo(Screen.AddDevice.route) { inclusive = true }
                                        }
                                    }
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Person, "Profile") },
                                    label = { Text("Profile") },
                                    selected = currentRoute == Screen.Profile.route,
                                    onClick = {
                                        navController.navigate(Screen.Profile.route) {
                                            popUpTo(Screen.Profile.route) { inclusive = true }
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { padding ->
                    AppNavigation(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    )
                }
            }
        }
    }
}
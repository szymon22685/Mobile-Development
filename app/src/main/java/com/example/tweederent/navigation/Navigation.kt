package com.example.tweederent.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tweederent.ui.screens.DiscoverScreen
import com.example.tweederent.ui.screens.LoginScreen
import com.example.tweederent.ui.screens.RegisterScreen

@Composable
fun AppNavigation(
    appState: AppState = rememberAppState(),
    navController: NavHostController
) {

    LaunchedEffect(appState.isAuthenticated) {
        if (!appState.isAuthenticated) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (appState.isAuthenticated) Screen.Discover.route else Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Discover.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Discover.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        // Voor deze screens worden er auth checks gedaan
        composable(Screen.Discover.route) {
            DiscoverScreen()
        }
    }
}
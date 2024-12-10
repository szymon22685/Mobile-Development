package com.example.tweederent.navigation

import AddDeviceScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.tweederent.ui.screens.DeviceDetailScreen
import com.example.tweederent.ui.screens.DiscoverScreen
import com.example.tweederent.ui.screens.LoginScreen
import com.example.tweederent.ui.screens.ProfileScreen
import com.example.tweederent.ui.screens.RegisterScreen

@Composable
fun AppNavigation(
    appState: AppState = rememberAppState(),
    navController: NavHostController,
    modifier: Modifier = Modifier
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
        startDestination = if (appState.isAuthenticated) Screen.Discover.route else Screen.Login.route,
        modifier = modifier
    ) {
        // Auth flow
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

        // Main app flow
        composable(Screen.Discover.route) {
            DiscoverScreen(
                onNavigate = { route ->
                    println("Navigating to: $route")
                    navController.navigate(route)
                }
            )
        }

        composable(Screen.AddDevice.route) {
            AddDeviceScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToDeviceDetail = { deviceId ->
                    navController.navigate(Screen.DeviceDetail.createRoute(deviceId))
                }
            )
        }

        // Device detail screen
        composable(
            route = "device_detail/{deviceId}",
            arguments = listOf(
                navArgument("deviceId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getString("deviceId")
            println("Opening device detail with ID: $deviceId")
            if (deviceId != null) {
                DeviceDetailScreen(
                    deviceId = deviceId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
package com.example.tweederent.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.tweederent.ui.screens.AddDeviceScreen
import com.example.tweederent.ui.screens.DeviceDetailScreen
import com.example.tweederent.ui.screens.DiscoverScreen
import com.example.tweederent.ui.screens.LoginScreen
import com.example.tweederent.ui.screens.PasswordResetScreen
import com.example.tweederent.ui.screens.ProfileScreen
import com.example.tweederent.ui.screens.RegisterScreen
import com.example.tweederent.ui.screens.ReviewScreen
import com.example.tweederent.ui.viewmodel.ReviewViewModel

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
                },
                onNavigateToPasswordReset = {
                    navController.navigate(Screen.PasswordReset.route)
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

        composable(Screen.PasswordReset.route) {
            PasswordResetScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Main app flow
        composable(Screen.Discover.route) {
            DiscoverScreen(
                onNavigate = { route ->
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
                },
                onNavigateToReview = { rentalId ->
                    navController.navigate(Screen.Review.createRoute(rentalId))
                }
            )
        }

        // Device detail screen
        composable(
            route = Screen.DeviceDetail.route,
            arguments = listOf(
                navArgument("deviceId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getString("deviceId")
            deviceId?.let {
                DeviceDetailScreen(
                    deviceId = it,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        // Review screen
        composable(
            route = Screen.Review.route,
            arguments = listOf(
                navArgument("rentalId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val rentalId = backStackEntry.arguments?.getString("rentalId")
            if (rentalId != null) {
                val viewModel: ReviewViewModel = viewModel()
                LaunchedEffect(Unit) {
                    viewModel.setNavigationCallback {
                        navController.popBackStack()
                    }
                }
                ReviewScreen(
                    rentalId = rentalId,
                    viewModel = viewModel
                )
            }
        }
    }
}
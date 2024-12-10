package com.example.tweederent.navigation

sealed class Screen(val route: String) {
    // Auth Flow
    object Login : Screen("login")
    object Register : Screen("register")
    object PasswordReset : Screen("password_reset")

    // Main Flow
    object Discover : Screen("discover")
    object AddDevice : Screen("add_device")
    object Profile : Screen("profile")

    // Detail Screens
    object DeviceDetail : Screen("device_detail/{deviceId}") {
        fun createRoute(deviceId: String) = "device_detail/$deviceId"
    }

    object Review : Screen("review/{rentalId}") {
        fun createRoute(rentalId: String) = "review/$rentalId"
    }

    object LocationPicker : Screen("location_picker") {
        const val RESULT_KEY = "selected_location"
    }
}
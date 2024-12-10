package com.example.tweederent.navigation

sealed class Screen(val route: String) {
    // Auth Flow
    object Login : Screen("login")
    object Register : Screen("register")

    // Main Flow (Bottom Navigation)
    object Discover : Screen("discover")
    object AddDevice : Screen("add_device")
    object Profile : Screen("profile")

    // Detail Screens
    object DeviceDetail : Screen("device_detail/{deviceId}") {
        fun createRoute(deviceId: String) = "device_detail/$deviceId"
    }

    object LocationPicker : Screen("location_picker") {
        const val RESULT_KEY = "selected_location"
    }

    // Profile Sub-screens
    object MyDevices : Screen("my_devices")
    object MyRentals : Screen("my_rentals")
    object UserReviews : Screen("user_reviews")
}
package com.example.pills.pills.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Loading : Screen("loading")

    object ConfigurationScreen : Screen("configuration")
    object FriendScreen : Screen("friends")
    object ResetPassword : Screen("resetPassword")
    object SetNewPassword : Screen("setNewPassword")

    object OtpVerification: Screen("otpVerify")
    object HomeScreen: Screen("homeScreen")
    object CalendarScreen: Screen("calendarScreen")
    object ProfileScreen: Screen("profileScreen")
    object EditProfileScreen: Screen("editProfileScreen")
    object HelpScreen: Screen("helpScreen")
}

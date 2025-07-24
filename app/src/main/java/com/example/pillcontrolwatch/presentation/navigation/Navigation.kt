package com.example.pills.pills.navigation

import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pills.pills.presentation.login.LoginScreen
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import com.example.pills.pills.presentation.homePage.HomeScreen



@Composable
fun AuthNavigation(
    startDestination: String
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavHost(navController = navController, startDestination = startDestination) {

        // Login Screen
        composable(
            route = Screen.Login.route
        ) {
            LoginScreen(
                navigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                navigateToForgetPassword = { navController.navigate(Screen.ResetPassword.route) },
                navigateToHome = {
                    navController.navigate(Screen.HomeScreen.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                },
                navigateToOtp = { email ->
                    navController.navigate("${Screen.OtpVerification.route}/$email/login")
                }
            )
        }

        // Home Screen
        composable(
            route = Screen.HomeScreen.route
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    HomeScreen(
                        navigateToLogin = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) // Clears the backstack if needed.
                            }
                        },
                        navigateToFriends = {
                            navController.navigate(Screen.FriendScreen.route) {
                                popUpTo(0) // Clears the backstack if needed.
                            }
                        }

                    )
                }
            }
        }









    }
}
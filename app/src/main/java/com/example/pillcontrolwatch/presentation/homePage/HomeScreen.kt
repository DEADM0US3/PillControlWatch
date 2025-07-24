package com.example.pills.pills.presentation.homePage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HomeScreen(
    navigateToLogin: () -> Unit,
    navigateToFriends : () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        HomeScreenUI(
            navigateToFriends
        )
    }
} 
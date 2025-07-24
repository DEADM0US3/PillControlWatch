package com.example.pills.pills.presentation.homePage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillcontrolwatch.domain.use_case.GetUserProfile
import com.example.pills.pills.domain.repository.LoginRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class HomeViewModel(
    private val loginRepository: LoginRepository,
    private val supabaseClient: SupabaseClient,
    private val getUserProfile: GetUserProfile
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadSessionData()
    }

    fun loadSessionData() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "Loading session data...")

                // Get the current session from Supabase
                val session = supabaseClient.auth.currentSessionOrNull()
                val userId = supabaseClient.auth.currentUserOrNull()?.id.toString()
                val access = session?.accessToken ?: "No Access Token"
                val refresh = session?.refreshToken ?: "No Refresh Token"

                Log.d("HomeViewModel", "Session found: ${session != null}")

                // Get complete user profile from database
                val profileResult = getUserProfile()

                if (profileResult.isSuccess) {
                    val userProfile = profileResult.getOrNull()
                    userProfile?.let { profile ->
                        _uiState.value = HomeUiState(
                            userId = userId, // Añade esto a tu HomeUiState
                            userName = profile.name ?: "Guest",
                            userEmail = profile.email,
                            userPhone = profile.phone,
                            userAge = profile.age,
                            accessToken = access,
                            refreshToken = refresh,
                            errorMessage = null,
                            isLoading = false
                        )
                    } ?: run {
                        val userResponse = supabaseClient.auth.retrieveUserForCurrentSession(updateSession = true)
                        val fullNameValue = userResponse.userMetadata?.get("full_name")
                        val userName = fullNameValue?.toString() ?: userResponse.email ?: "Guest"

                        _uiState.value = HomeUiState(
                            userId = userId, // Añade esto
                            userName = userName,
                            userEmail = userResponse.email ?: "",
                            accessToken = access,
                            refreshToken = refresh,
                            errorMessage = null,
                            isLoading = false
                        )
                    }
                } else {
                    Log.d("HomeViewModel", "Profile fetch failed: ${profileResult.exceptionOrNull()?.message}")
                    // Fallback to basic auth data if profile fetch failed
                    val userResponse = supabaseClient.auth.retrieveUserForCurrentSession(updateSession = true)
                    val fullNameValue = userResponse.userMetadata?.get("full_name")
                    val userName = fullNameValue?.toString() ?: userResponse.email ?: "Guest"

                    _uiState.value = HomeUiState(
                        userId = userId, // Añade esto
                        userName = userName,
                        userEmail = userResponse.email ?: "",
                        accessToken = access,
                        refreshToken = refresh,
                        errorMessage = "Could not load complete profile: ${profileResult.exceptionOrNull()?.message}",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading session data: ${e.message}")
                val session = supabaseClient.auth.currentSessionOrNull()
                val access = session?.accessToken ?: "No Access Token"
                val refresh = session?.refreshToken ?: "No Refresh Token"
                _uiState.value = HomeUiState(
                    userName = "Guest",
                    accessToken = access,
                    refreshToken = refresh,
                    errorMessage = "Failed to fetch user details: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = loginRepository.logoutUser()
            if (result.isSuccess) {
                onLogoutSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Logout failed. Please try again."
                )
            }
        }
    }

    /**
     * Refresh user profile data
     * Call this method when returning from profile editing
     */
    fun refreshUserProfile() {
        loadSessionData()
    }
}

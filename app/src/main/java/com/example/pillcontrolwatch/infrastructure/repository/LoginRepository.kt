package com.example.pills.pills.domain.repository

import android.app.Activity
import android.content.Context
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID


private const val WEB_CLIENT_ID = ""  /*ENTER THE WEB_CLIENT_ID HERE COPIED FROM GOOGLE CONSOLE*/

class LoginRepository(
    private val context: Context,
    private val supabaseClient: SupabaseClient
) {

    suspend fun loginUser(email: String, password: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Sign in using Supabase Auth with the Email provider.
                supabaseClient.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }

                // Retrieve the current session and user.
                val session = supabaseClient.auth.currentSessionOrNull()
                val user = supabaseClient.auth.retrieveUserForCurrentSession()

                if (user != null && session != null) {
                    // No need to manually persist the tokens; Supabase now handles storage.
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Invalid credentials. Please try again."))
                }
            } catch (e: Exception) {
                if (e.message?.contains("Email not confirmed") == true) {
                    // If the email isn’t confirmed, trigger resend.
                    supabaseClient.auth.resendEmail(OtpType.Email.SIGNUP, email)
                    Result.failure(Exception("Email not verified. Verification email sent."))
                } else {
                    Result.failure(e)
                }
            }
        }
    }

    suspend fun logoutUser(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                supabaseClient.auth.signOut()
                Result.success(Unit)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

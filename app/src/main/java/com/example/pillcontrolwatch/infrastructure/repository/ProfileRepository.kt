package com.example.pills.pills.domain.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import kotlinx.serialization.Serializable

class ProfileRepository(
    private val supabaseClient: SupabaseClient,
    private val context: Context
) {

    @Serializable
    data class UserProfileDto(
        val id: String,
        val email: String,
        val name: String? = null,
        val phone: String? = null,
        val age: String? = null,
        val created_at: String? = null,
        val updated_at: String? = null
    )


    data class UserProfile(
        val id: String,
        val email: String,
        val name: String?,
        val phone: String?,
        val age: String?,
        val createdAt: String?,
        val updatedAt: String?
    )

    suspend fun getUserProfile(): Result<UserProfile> = withContext(Dispatchers.IO) {
        try {
            val user = supabaseClient.auth.currentUserOrNull()
                ?: return@withContext Result.failure(Exception("No authenticated user"))

            val profile = supabaseClient
                .from("users")
                .select(
                    Columns.list("id", "email", "name", "created_at", "updated_at")
                ) {
                    filter { eq("id", user.id) }
                }
                .decodeList<UserProfileDto>()
                .firstOrNull() ?: return@withContext Result.failure(Exception("User profile not found"))

            Result.success(
                UserProfile(
                    id = profile.id,
                    email = profile.email,
                    name = profile.name,
                    phone = profile.phone,
                    age = profile.age,
                    createdAt = profile.created_at,
                    updatedAt = profile.updated_at
                )
            )

        } catch (e: Exception) {
            Log.e("ProfileRepository", "getUserProfile error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun updateUserProfile(
        fullName: String,
        email: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (fullName.isBlank() || email.isBlank())
                return@withContext Result.failure(Exception("Full name and email are required"))

            val user = supabaseClient.auth.currentUserOrNull()
                ?: return@withContext Result.failure(Exception("No authenticated user"))

            // 🔄 Actualizar en Supabase Auth (nombre y correo)
            supabaseClient.auth.updateUser {
                this.email = email
                this.data = buildJsonObject {
                    put("name", fullName)
                }
            }

            // 🔄 Actualizar en tabla "users"
            supabaseClient.from("users").update(
                mapOf(
                    "name" to fullName,
                    "email" to email
                )
            ) {
                filter { eq("id", user.id) }
            }

            Log.d("ProfileRepository", "User profile updated: ${user.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ProfileRepository", "updateUserProfile error: ${e.message}")
            Result.failure(e)
        }
    }



    suspend fun updateProfileImage(imageUrl: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (imageUrl.isBlank())
                return@withContext Result.failure(Exception("Image URL is required"))

            val user = supabaseClient.auth.currentUserOrNull()
                ?: return@withContext Result.failure(Exception("No authenticated user"))

            supabaseClient.from("users").update(
                mapOf("profile_image_url" to imageUrl)
            ) {
                filter { eq("id", user.id) }
            }

            Log.d("ProfileRepository", "Profile image updated for user: ${user.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ProfileRepository", "updateProfileImage error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun uploadProfileImage(imageUri: String): Result<String> = withContext(Dispatchers.IO) {
        var tempFile: File? = null
        try {
            if (imageUri.isBlank())
                return@withContext Result.failure(Exception("Image URI is required"))

            val user = supabaseClient.auth.currentUserOrNull()
                ?: return@withContext Result.failure(Exception("No authenticated user"))

            val uri = Uri.parse(imageUri)
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext Result.failure(Exception("Failed to open image file"))

            tempFile = File.createTempFile("profile_", ".jpg", context.cacheDir).apply {
                FileOutputStream(this).use { output -> inputStream.copyTo(output) }
            }

            if (tempFile.length() > 5 * 1024 * 1024)
                return@withContext Result.failure(Exception("Image file too large (max 5MB)"))

            val fileName = "${user.id}/profile_${UUID.randomUUID()}.jpg"

            supabaseClient.storage
                .from("profile-images")
                .upload(fileName, tempFile.readBytes())

            val publicUrl = supabaseClient.storage
                .from("profile-images")
                .publicUrl(fileName)

            Log.d("ProfileRepository", "Uploaded image: $publicUrl")
            Result.success(publicUrl)
        } catch (e: Exception) {
            Log.e("ProfileRepository", "uploadProfileImage error: ${e.message}")
            Result.failure(e)
        } finally {
            tempFile?.delete()
        }
    }

    suspend fun deleteProfileImage(imageUrl: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (imageUrl.isBlank())
                return@withContext Result.failure(Exception("Image URL is required"))

            val filePath = imageUrl.substringAfter("profile-images/")

            if (filePath.isBlank())
                return@withContext Result.failure(Exception("Invalid image URL"))

            supabaseClient.storage
                .from("profile-images")
                .delete(filePath)

            Log.d("ProfileRepository", "Deleted image: $filePath")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ProfileRepository", "deleteProfileImage error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun doesUserProfileExist(userId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            if (userId.isBlank()) return@withContext false

            val result = supabaseClient.from("users")
                .select(Columns.list("id")) {
                    filter { eq("id", userId) }
                }
                .decodeList<Map<String, String>>()

            result.isNotEmpty()
        } catch (e: Exception) {
            Log.e("ProfileRepository", "doesUserProfileExist error: ${e.message}")
            false
        }
    }

    suspend fun createUserProfile(
        userId: String,
        email: String,
        fullName: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (userId.isBlank() || email.isBlank() || fullName.isBlank())
                return@withContext Result.failure(Exception("All fields are required"))

            supabaseClient.from("users").insert(
                mapOf(
                    "id" to userId,
                    "email" to email,
                    "full_name" to fullName
                )
            )

            Log.d("ProfileRepository", "Created user profile: $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ProfileRepository", "createUserProfile error: ${e.message}")
            Result.failure(e)
        }
    }
}

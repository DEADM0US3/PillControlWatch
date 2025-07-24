package com.example.pills.pills.domain.repository

import android.util.Log
import com.example.pills.pills.domain.entities.Friend
import com.example.pills.pills.domain.entities.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable


@Serializable
data class FriendWithCycleInfo(
    val user_id: String,
    val friend_id: String,
    val friend_user_id: String,
    val name: String,

    // Ciclo más reciente
    val recent_cycle_id: String?,
    val start_date: String?,        // o LocalDate
    val end_date: String?,
    val pill_count: Int?,
    val current_day: Int?,
    val take_hour: String?,

    // Pastilla de hoy
    val pill_id_today: String?,
    val pill_status_today: String?, // "taken", "skipped" o null
    val pill_taken_date: String?    // o LocalDate
)


class FriendRepository(private val supabaseClient: SupabaseClient) {

    suspend fun addFriend(userId: String?, friendId: String): Result<Unit> = withContext(Dispatchers.IO) {
        if (userId == null) return@withContext Result.failure(IllegalArgumentException("userId es null"))

        return@withContext try {
            val friend = Friend(user_id = userId, friend_id = friendId)
            supabaseClient.from("friends").insert(friend)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFriends(userId: String): Result<List<FriendWithCycleInfo>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = supabaseClient
                .from("friends_with_user")
                .select{
                    filter { eq("user_id", userId) }
                }
                .decodeList<FriendWithCycleInfo>()

            Log.d("FriendRepository", "Friends with user info for $userId: $response")
            Result.success(response)
        } catch (e: Exception) {
            Log.e("FriendRepository", "Error fetching friends", e)
            Result.failure(e)
        }
    }

    suspend fun getUserIdByEmail(email: String): Result<String> = withContext(Dispatchers.IO) {
        return@withContext runCatching {
            val user = supabaseClient.from("users")
                .select {
                    filter { eq("email", email) }
                }
                .decodeSingle<User>()

            user.id
        } as Result<String>
    }
}
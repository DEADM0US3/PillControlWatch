package com.example.pills.pills.domain.repository

import android.util.Log
import com.example.pills.pills.domain.entities.Cycle
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class CycleRepository(private val supabaseClient: SupabaseClient) {

    suspend fun createCycle(
        userId: String,
        startDate: String,
        pillCount: Int = 21,
        takeHour: String? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val cycle = Cycle(
                user_id = userId,
                start_date = startDate,
                pill_count = pillCount,
                take_hour = takeHour
            )
            Log.d("CycleRepository", "Cycle for creation: $cycle")

            val result =supabaseClient.postgrest
                .from("cycles")
                .insert(cycle)

            Log.d("CycleRepository", "Cycle created: $result")

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getActiveCycle(userId: String): Result<Cycle> = withContext(Dispatchers.IO) {

        val today = java.time.LocalDate.now().toString()

        try {
            val result = supabaseClient.from("cycles").select {
                filter {
                    gt("end_date", today)
                    eq("user_id", userId)
                    eq("is_deleted", false)
                }
                limit(1)
            }.decodeSingle<Cycle>()

            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCycle(cycleId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.from("cycles").update(
                mapOf("is_deleted" to true)
            ) {
                filter { eq("id", cycleId) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllCycles(userId: String): Result<List<Cycle>> = withContext(Dispatchers.IO) {
        try {
            val result = supabaseClient.from("cycles").select {
                filter { eq("user_id", userId) }
            }.decodeList<Cycle>()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


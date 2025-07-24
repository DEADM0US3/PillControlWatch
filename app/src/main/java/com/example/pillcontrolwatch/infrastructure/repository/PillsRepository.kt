package com.example.pills.pills.domain.repository

import android.util.Log
import com.example.pills.pills.domain.entities.Pill
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID


@Serializable
data class CreatePill(
    val cycle_id: String,
    val user_id: String,
    val day_taken: String,
    val hour_taken: String? = null,
    val status: String,
    val complications: String? = null,
)
class PillRepository(private val supabaseClient: SupabaseClient) {

    private val formatter = DateTimeFormatter.ISO_DATE

    private suspend fun existsPill(userId: String, date: String): Boolean {
        val result = supabaseClient.from("pills").select {
            filter {
                eq("user_id", userId)
                eq("day_taken", date)
            }
        }.decodeList<Pill>()

        return result.size > 0
    }

    suspend fun takePill(
        userId: String,
        cycleId: String,
        date: String,
        hour: String? = null,
        status: String,
        complications: String? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        Result.runCatching {

            Log.d("PillRepository", "Taking pill for user: $userId on date: $date with status: $status")

            Log.d("PillRepository", "Is not taken: $userId on date: $date with status: $status")

            val data = CreatePill(
                cycle_id = cycleId,
                user_id = userId,
                day_taken = date,
                hour_taken = hour,
                status = status,
                complications = complications
            )

            val response = supabaseClient
                .postgrest
                .from("pills")
                .insert(data)

            Log.d("PillRepository", "Insert response: ${response.data}")

            supabaseClient
                .postgrest
                .from("pills")
                .insert(data)


            Unit
        }
    }

    suspend fun getPillByDate(userId: String, date: String): Result<Pill> =
        withContext(Dispatchers.IO) {
            Result.runCatching {
                supabaseClient.from("pills").select {
                    filter {
                        eq("user_id", userId)
                        eq("day_taken", date)
                    }
                }.decodeSingle()
            }
        }

    suspend fun getPillsInACycle(userId: String, cycle_id: String): Result<List<Pill>> =
        withContext(Dispatchers.IO) {
            Result.runCatching {
                supabaseClient.from("pills").select {
                    filter {
                        eq("user_id", userId)
                        eq("cycle_id", cycle_id)
                    }
                }.decodeList()
            }
        }


    suspend fun getPillsInMonth(userId: String, year: Int, month: Int): Result<List<Pill>> =
        withContext(Dispatchers.IO) {
            Result.runCatching {
                val startDate = LocalDate.of(year, month, 1).format(formatter)
                val endDate = LocalDate.of(year, month, 28).plusDays(3).format(formatter)

                supabaseClient.from("pills").select {
                    filter {
                        eq("user_id", userId)
                        gte("day_taken", startDate)
                        lte("day_taken", endDate)
                    }
                }.decodeList()
            }
        }


    suspend fun editPill(
        pillId: String,
        hour_taken: String?,
        status: String? = null,
        complications: String? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        Result.runCatching {
            val updates = buildMap {
                status?.let { put("status", it) }
                complications?.let { put("complications", it) }
                hour_taken?.let { put("hour_taken", it) }
            }

            if (updates.isEmpty()) throw IllegalArgumentException("No hay cambios para aplicar")

            supabaseClient.from("pills").update(updates) {
                filter { eq("id", pillId) }
            }

            Unit
        }
    }

    suspend fun deletePill(pillId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            Result.runCatching {
                supabaseClient.from("pills").delete {
                    filter { eq("id", pillId) }
                }

                Unit
            }
        }
}

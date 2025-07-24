package com.example.pills.pills.domain.repository

import com.example.pills.pills.domain.entities.BaseEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.PostgrestFilterDSL
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.*
import io.github.jan.supabase.postgrest.query.filter.PostgrestFilterBuilder


class GenericRepository(
    val supabaseClient: SupabaseClient
) {

    suspend inline fun <reified T : BaseEntity> getAll(tableName: String): Result<List<T>> {
        return runCatching {
            supabaseClient.from(tableName)
                .select()
                .decodeList<T>()
        }
    }

    suspend inline fun <reified T : BaseEntity> getById(tableName: String, id: String): Result<T?> {
        return runCatching {
            supabaseClient.from(tableName)
                .select{
                    filter{
                        eq("id", id)
                    }
                }
                .decodeSingleOrNull<T>()
        }
    }

    suspend inline fun <reified T : BaseEntity> getByFields(
        tableName: String,
        crossinline filter: @PostgrestFilterDSL () -> Unit
    ): Result<List<T>> {
        return runCatching {
            supabaseClient.from(tableName)
                .select {
                    filter()
                }
                .decodeList<T>()
        }
    }

    suspend inline fun <reified T : BaseEntity> insert(tableName: String, entity: T): Result<T> {
        return runCatching {
            supabaseClient.from(tableName)
                .insert(entity)
                .decodeSingle<T>()
        }
    }

    suspend inline fun <reified T : BaseEntity> update(tableName: String, entity: T): Result<T> {
        return runCatching {
            supabaseClient.from(tableName)
                .update(entity)
                {
                    filter {
                        eq("id", entity.id.toString())
                    }
                }
                .decodeSingle<T>()
        }
    }

    suspend fun deleteById(tableName: String, id: String): Result<Unit> {
        return runCatching {
            supabaseClient.from(tableName)
                .delete {
                    filter {
                        eq("id", id)
                    }
                }
            Unit
        }
    }

    suspend inline fun <reified T : BaseEntity> delete(tableName: String, entity: T): Result<Unit> {
        return deleteById(tableName, entity.id.toString())
    }
}

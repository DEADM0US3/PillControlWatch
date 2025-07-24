package com.example.pills.pills.domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class Pill(
    val id: String?,
    val cycle_id: String,
    val user_id: String,
    val day_taken: String,
    val hour_taken: String?,
    val status: String, // 'taken' o 'skipped'
    val complications: String?,
    val created_at: String?
)

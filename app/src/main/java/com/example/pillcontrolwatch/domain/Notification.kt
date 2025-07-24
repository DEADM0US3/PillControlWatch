package com.example.pills.pills.domain.entities

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Notification(
    val id: String?,
    val sender_id: String,
    val receiver_id: String,
    val message: String,
    val created_at: String?
)

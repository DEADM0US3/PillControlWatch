package com.example.pills.pills.domain.entities

import kotlinx.serialization.Serializable


@Serializable
data class Friend(
    val id: String? = null,
    val user_id: String,
    val friend_id: String,
    val created_at: String? = null
)
package com.example.pills.pills.domain.entities

import kotlinx.serialization.Serializable

@Serializable
open class BaseEntity (
    val id: String? = null,
    val createdAt: String? = null,
    val is_deleted: Boolean = false
)
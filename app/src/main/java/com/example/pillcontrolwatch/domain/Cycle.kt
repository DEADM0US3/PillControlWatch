package com.example.pills.pills.domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class Cycle(
    val user_id: String,
    val start_date: String,
    val pill_count: Int = 21,
    val end_date: String? = null ,
    val current_day: Int = 1,
    val take_hour: String? = null,
) : BaseEntity()


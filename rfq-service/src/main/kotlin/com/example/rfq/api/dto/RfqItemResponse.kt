package com.example.rfq.api.dto

import java.time.Instant
import java.util.UUID

data class RfqItemResponse(
    val rfqId: UUID,
    val title: String,
    val description: String,
    val deliveryLocation: String,
    val createdAt: Instant,
    val type: String? = null,
)

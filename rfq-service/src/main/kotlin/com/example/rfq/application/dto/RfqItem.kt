package com.example.rfq.application.dto

import java.time.Instant
import java.util.UUID

// todo: this can be deleted and used domain.model.Rfq instead

data class RfqItem(
    val rfqId: UUID,
    val title: String,
    val description: String,
    val deliveryLocation: String,
    val createdAt: Instant,
    val type: String? = null,
)

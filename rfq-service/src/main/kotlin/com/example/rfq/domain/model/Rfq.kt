package com.example.rfq.domain.model

import java.time.Instant
import java.util.UUID

data class Rfq(
    val rfqId: UUID,
    val title: String,
    val description: String,
    val deliveryLocation: String,
    val productType: String,
    val status: RfqStatus,
    val createdAt: Instant,
)

enum class RfqStatus {
    NEW,
    PROCESSED,
}

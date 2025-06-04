package com.example.rfq.infrastructure.persistence.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("rfq_core")
data class RfqCoreEntity(
    @Id
    val rfqId: UUID,
    val title: String,
    val description: String,
    val deliveryLocation: String,
    val productType: String,
    val status: String,
    val created: Instant,
    val updated: Instant? = null,
)

@Table("matching_core")
data class MatchingCoreEntity(
    @Id
    val matchingId: UUID,
    val rfqId: UUID,
    val supplierId: UUID,
    val status: String?,
    val created: Instant,
    val updated: Instant? = null,
)

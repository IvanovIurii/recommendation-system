package com.example.rfq.application.dto

import java.time.Instant
import java.util.UUID

data class MatchedSupplierItem(
    val id: UUID,
    val name: String,
    val created: Instant,
)

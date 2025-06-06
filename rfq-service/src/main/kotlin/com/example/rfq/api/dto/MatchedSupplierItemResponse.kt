package com.example.rfq.api.dto

import java.time.Instant
import java.util.UUID

class MatchedSupplierItemResponse(
    val id: UUID,
    val name: String,
    val created: Instant,
)

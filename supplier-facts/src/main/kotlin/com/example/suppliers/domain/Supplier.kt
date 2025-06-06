package com.example.suppliers.domain

import java.time.Instant
import java.util.UUID

class Supplier(
    val supplierId: UUID,
    val supplierName: String,
    val deliveryArea: List<String>,
    val createdAt: Instant,
)
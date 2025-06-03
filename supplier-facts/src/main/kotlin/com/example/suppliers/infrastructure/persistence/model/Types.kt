package com.example.suppliers.infrastructure.persistence.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("supplier")
data class SupplierEntity(
    @Id
    val supplierId: UUID,
    val supplierName: String,
    val deliveryArea: List<String>,
    val createdAt: Instant,
    val updatedAt: Instant? = null,
)

@Table("supplier_product")
data class ProductEntity(
    @Id
    val productId: UUID,
    val productName: String,
    val productDescription: String,
    val productType: String,
    val supplierId: UUID,
    val createdAt: Instant,
    val updatedAt: Instant? = null,
)

data class SupplierProductAggregate(
    val supplierId: UUID? = null,
    val productId: UUID? = null,
    val supplierName: String,
    val deliveryArea: List<String>,
    val productName: String,
    val productDescription: String,
    val productType: String,
)

package com.example.suppliers.infrastructure.persistence.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("supplier_product")
data class SupplierProductEntity(
    @Id
    val id: UUID,
    val productName: String,
    val productDescription: String,
    val productType: String,
    val supplierName: String,
    val deliveryArea: List<String>,
)

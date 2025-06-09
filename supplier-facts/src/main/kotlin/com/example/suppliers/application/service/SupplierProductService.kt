package com.example.suppliers.application.service

import com.example.suppliers.domain.Supplier
import com.example.suppliers.infrastructure.persistence.model.SupplierProductAggregate
import com.example.suppliers.infrastructure.persistence.repository.SupplierProductRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SupplierProductService(
    private val supplierProductRepository: SupplierProductRepository,
) {
    fun getAll(pageable: Pageable): Page<SupplierProductAggregate> {
        return supplierProductRepository.findAll(pageable)
    }

    fun getSupplier(supplierId: UUID): Supplier? {
        return supplierProductRepository.findById(supplierId)?.let { entity ->
            Supplier(
                supplierId = entity.supplierId,
                supplierName = entity.supplierName,
                deliveryArea = entity.deliveryArea,
                createdAt = entity.createdAt,
            )
        }
    }

    fun getSupplier(name: String): Supplier? {
        return supplierProductRepository.findByName(name)?.let { entity ->
            Supplier(
                supplierId = entity.supplierId,
                supplierName = entity.supplierName,
                deliveryArea = entity.deliveryArea,
                createdAt = entity.createdAt,
            )
        }
    }
}

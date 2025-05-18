package com.example.suppliers.application.service

import com.example.suppliers.infrastructure.persistence.model.SupplierProductEntity
import com.example.suppliers.infrastructure.persistence.repository.SupplierProductRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class SupplierProductService(
    private val supplierProductRepository: SupplierProductRepository,
) {
    fun getAll(pageable: Pageable): Page<SupplierProductEntity> {
        return supplierProductRepository.findAll(pageable)
    }
}
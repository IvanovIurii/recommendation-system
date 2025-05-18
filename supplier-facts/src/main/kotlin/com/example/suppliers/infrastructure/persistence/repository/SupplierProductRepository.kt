package com.example.suppliers.infrastructure.persistence.repository

import com.example.suppliers.infrastructure.persistence.model.SupplierProductEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.stereotype.Repository

@Repository
class SupplierProductRepository(
    private val supplierProductEntityRepository: SupplierProductEntityRepository,
    private val jdbcAggregateTemplate: JdbcAggregateTemplate,
) {
    // todo: consider using domain object
    // todo: maybe to use 2 tables after all, Supplier + Product
    fun saveAll(entities: List<SupplierProductEntity>) {
        jdbcAggregateTemplate.insertAll(entities)
    }

    fun findAll(pageable: Pageable) = supplierProductEntityRepository.findAll(pageable)
}
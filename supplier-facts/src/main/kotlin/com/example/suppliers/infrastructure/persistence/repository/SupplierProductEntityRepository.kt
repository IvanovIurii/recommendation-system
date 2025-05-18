package com.example.suppliers.infrastructure.persistence.repository

import com.example.suppliers.infrastructure.persistence.model.SupplierProductEntity
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.UUID

interface SupplierProductEntityRepository : PagingAndSortingRepository<SupplierProductEntity, UUID>
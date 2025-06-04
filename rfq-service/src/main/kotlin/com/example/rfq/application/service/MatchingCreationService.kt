package com.example.rfq.application.service

import com.example.rfq.api.MatchedSupplierDto
import com.example.rfq.infrastructure.persistence.model.MatchingCoreEntity
import com.example.rfq.infrastructure.persistence.repository.MatchingCoreRepository
import com.example.rfq.infrastructure.persistence.repository.RfqCoreRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class MatchingCreationService(
    private val matchingCoreRepository: MatchingCoreRepository,
    private val rfqCoreRepository: RfqCoreRepository,
) {
    // todo: we have to check here that this supplier was already added to this rfq
    fun addSuppliers(rfqId: UUID, suppliers: List<UUID>) {
        val rfq = rfqCoreRepository.findById(rfqId)
            ?: throw RuntimeException("RFQ has not been found by id: $rfqId")

        suppliers.forEach { supplierId ->
            val entity = MatchingCoreEntity(
                matchingId = UUID.randomUUID(),
                rfqId = rfq.rfqId,
                // todo: use enum
                status = "CREATED",
                created = Instant.now(),
                supplierId = supplierId
            )
            matchingCoreRepository.save(entity)
        }
    }

    fun getSuppliers(rfqId: UUID): List<MatchedSupplierDto> {
        // todo: we need to call supplier-facts API
        return matchingCoreRepository.findAll(rfqId).map { entity ->
            // todo: add and render timestamp on UI as well
            MatchedSupplierDto(
                id = entity.matchingId,
                name = "NAME", // supplier name
                description = "DESCRIPTION", // todo: looks like not needed
                status = entity.status ?: "N/A",
            )
        }
    }
}
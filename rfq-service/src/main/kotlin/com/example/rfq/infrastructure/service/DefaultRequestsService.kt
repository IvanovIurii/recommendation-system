package com.example.rfq.infrastructure.service

import com.example.rfq.domain.RequestsService
import com.example.rfq.domain.Rfq
import com.example.rfq.infrastructure.persistence.model.RfqCoreEntity
import com.example.rfq.infrastructure.persistence.repository.RfqCoreRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class DefaultRequestsService(
    private val rfqCoreRepository: RfqCoreRepository,
) : RequestsService {
    override fun createRfq(rfq: Rfq) {
        val rfqEntity = RfqCoreEntity(
            rfqId = UUID.randomUUID(),
            title = rfq.title,
            description = rfq.description,
            deliveryLocation = rfq.deliveryLocation,
            created = Instant.now()
        )
        rfqCoreRepository.save(rfqEntity)
    }

    override fun listAll(): List<RfqCoreEntity> {
        return rfqCoreRepository.findAll()
    }
}
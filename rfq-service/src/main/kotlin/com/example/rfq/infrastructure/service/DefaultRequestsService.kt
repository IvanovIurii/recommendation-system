package com.example.rfq.infrastructure.service

import com.example.rfq.application.RequestsService
import com.example.rfq.application.dto.CreateRfqCommand
import com.example.rfq.application.dto.RfqItem
import com.example.rfq.domain.model.RfqStatus
import com.example.rfq.infrastructure.persistence.model.RfqCoreEntity
import com.example.rfq.infrastructure.persistence.repository.RfqCoreRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class DefaultRequestsService(
    private val rfqCoreRepository: RfqCoreRepository,
) : RequestsService {
    override fun createRfq(command: CreateRfqCommand) {
        val rfqEntity =
            RfqCoreEntity(
                rfqId = UUID.randomUUID(),
                title = command.title,
                description = command.description,
                deliveryLocation = command.deliveryLocation,
                productType = command.productType,
                created = Instant.now(),
                status = RfqStatus.NEW.name,
            )
        rfqCoreRepository.save(rfqEntity)
    }

    override fun listAll(): List<RfqItem> {
        return rfqCoreRepository.findAll().map { entity ->
            RfqItem(
                rfqId = entity.rfqId,
                title = entity.title,
                description = entity.description,
                deliveryLocation = entity.deliveryLocation,
                createdAt = entity.created,
            )
        }
    }
}

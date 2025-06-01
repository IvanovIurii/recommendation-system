package com.example.rfq.application.service

import com.example.rfq.domain.RequestsService
import com.example.rfq.domain.ResponseRfqDto
import org.springframework.stereotype.Service

@Service
class RfqListingService(
    private val requestsService: RequestsService,
) {
    fun listAll(): List<ResponseRfqDto> {
        return requestsService.listAll().map { entity ->
            ResponseRfqDto(
                rfqId = entity.rfqId,
                title = entity.title,
                description = entity.description,
                deliveryLocation = entity.deliveryLocation
            )
        }
    }

    private companion object {
        // todo: add logger
    }
}
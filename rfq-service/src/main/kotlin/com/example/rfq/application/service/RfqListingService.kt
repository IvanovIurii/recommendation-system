package com.example.rfq.application.service

import com.example.rfq.api.dto.RfqItemResponse
import com.example.rfq.application.RequestsService
import com.example.rfq.application.dto.RfqItem
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class RfqListingService(
    private val requestsService: RequestsService,
) {
    fun listAll(): List<RfqItem> {
        return requestsService.listAll()
    }

    fun getById(rfqId: UUID): RfqItem? {
        return requestsService.getById(rfqId = rfqId)
    }

    private companion object {
        // todo: add logger
    }
}

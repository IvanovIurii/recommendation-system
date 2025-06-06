package com.example.rfq.application.service

import com.example.rfq.application.RequestsService
import com.example.rfq.application.dto.RfqItem
import org.springframework.stereotype.Service

@Service
class RfqListingService(
    private val requestsService: RequestsService,
) {
    fun listAll(): List<RfqItem> {
        return requestsService.listAll()
    }

    private companion object {
        // todo: add logger
    }
}

package com.example.rfq.application.service

import com.example.rfq.domain.RequestsService
import com.example.rfq.domain.Rfq
import org.springframework.stereotype.Service

@Service
class RfqCreationService(
    private val requestsService: RequestsService,
) {
    fun createRfq(createRfqRequest: Rfq) {
        requestsService.createRfq(createRfqRequest)
    }

    private companion object {
        // todo: add logger
    }
}
package com.example.rfq.application.service

import com.example.rfq.application.RequestsService
import com.example.rfq.application.dto.CreateRfqCommand
import org.springframework.stereotype.Service

@Service
class RfqCreationService(
    private val requestsService: RequestsService,
) {
    fun createRfq(createRfqCommand: CreateRfqCommand) {
        requestsService.createRfq(createRfqCommand)
    }

    private companion object {
        // todo: add logger
    }
}

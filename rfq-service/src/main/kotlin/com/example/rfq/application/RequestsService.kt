package com.example.rfq.application

import com.example.rfq.application.dto.CreateRfqCommand
import com.example.rfq.application.dto.RfqItem
import java.util.UUID

interface RequestsService {
    fun createRfq(command: CreateRfqCommand)

    fun listAll(): List<RfqItem>

    fun getById(rfqId: UUID): RfqItem?
}

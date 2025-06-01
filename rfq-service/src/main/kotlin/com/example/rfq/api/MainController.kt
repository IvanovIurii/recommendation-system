package com.example.rfq.api

import com.example.rfq.application.service.RfqCreationService
import com.example.rfq.application.service.RfqListingService
import com.example.rfq.domain.ResponseRfqDto
import com.example.rfq.domain.Rfq
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

typealias CreateRfqRequest = Rfq

@RestController
@RequestMapping("/internal_api/")
class MainController(
    private val rfqCreationService: RfqCreationService,
    private val rfqListingService: RfqListingService,
) {
    @PostMapping("/rfqs")
    fun createRfq(
        @RequestBody createRfqRequest: CreateRfqRequest,
    ): ResponseEntity<String> {
        rfqCreationService.createRfq(createRfqRequest)
        return ResponseEntity.ok("RFQ has been created")
    }

    // todo: consider to have a pagination if too many
    @GetMapping("/rfqs")
    fun listAll(): ResponseEntity<List<ResponseRfqDto>> {
        return ResponseEntity.ok(rfqListingService.listAll())
    }
}

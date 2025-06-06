package com.example.rfq.api

import com.example.rfq.api.dto.CreateRfqRequest
import com.example.rfq.api.dto.RfqItemResponse
import com.example.rfq.api.dto.toCommand
import com.example.rfq.api.dto.toResponse
import com.example.rfq.application.service.RfqCreationService
import com.example.rfq.application.service.RfqListingService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal_api/")
class RfqController(
    private val rfqCreationService: RfqCreationService,
    private val rfqListingService: RfqListingService,
) {
    @PostMapping("/rfqs")
    fun createRfq(
        // todo: add @Valid
        @RequestBody createRfqRequest: CreateRfqRequest,
    ): ResponseEntity<String> {
        val command = createRfqRequest.toCommand()
        // todo: handle exceptions with controller advice
        rfqCreationService.createRfq(command)
        return ResponseEntity.ok("RFQ has been created")
    }

    // todo: consider to have a pagination if too many
    @GetMapping("/rfqs")
    fun listAll(): ResponseEntity<List<RfqItemResponse>> {
        return ResponseEntity.ok(
            rfqListingService.listAll().map { item ->
                item.toResponse()
            },
        )
    }
}

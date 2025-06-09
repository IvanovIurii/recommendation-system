package com.example.rfq.api

import com.example.rfq.api.dto.CreateRfqRequest
import com.example.rfq.api.dto.RfqItemResponse
import com.example.rfq.api.dto.toCommand
import com.example.rfq.api.dto.toResponse
import com.example.rfq.application.service.RfqCreationService
import com.example.rfq.application.service.RfqListingService
import com.example.rfq.infrastructure.clients.RecommendationServiceClientService
import com.example.rfq.infrastructure.clients.RecommendedSupplierResponseDto
import com.example.rfq.infrastructure.clients.SupplierFactsClientService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/internal_api/")
class RfqController(
    private val rfqCreationService: RfqCreationService,
    private val rfqListingService: RfqListingService,
    // this is terrible :((((
    private val supplierFactsClientService: SupplierFactsClientService,

    // todo: refactor
    private val recommendationServiceClientService: RecommendationServiceClientService,
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

    @GetMapping("/rfqs/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<RfqItemResponse> {
        return ResponseEntity.ok(
            // todo: make a new service
            rfqListingService.getById(rfqId = id)?.let {
                RfqItemResponse(
                    rfqId = it.rfqId,
                    title = it.title,
                    description = it.description,
                    deliveryLocation = it.deliveryLocation,
                    createdAt = it.createdAt,
                    type = it.type
                )
            }
        )
    }

    @GetMapping("/rfqs/{rfqId}/suppliers/recommend")
    fun recommend(@PathVariable rfqId: UUID): ResponseEntity<List<RecommendedSupplierResponseDto>?> {
        return ResponseEntity.ok(
            recommendationServiceClientService.getRecommendations(rfqId)?.map { item ->
                try {
                    val supplier = supplierFactsClientService.getSupplierByName(item.supplierName)
                    item.copy(supplierId = supplier!!.supplierId)
                } catch (e: Exception) {
                    item.copy(supplierId = null)
                }
            }
        )
    }
}

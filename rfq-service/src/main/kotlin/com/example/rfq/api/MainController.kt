package com.example.rfq.api

import com.example.rfq.application.service.MatchingCreationService
import com.example.rfq.application.service.RfqCreationService
import com.example.rfq.application.service.RfqListingService
import com.example.rfq.domain.ResponseRfqDto
import com.example.rfq.domain.Rfq
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import javax.management.Descriptor

typealias CreateRfqRequest = Rfq

@RestController
@RequestMapping("/internal_api/")
class MainController(
    private val rfqCreationService: RfqCreationService,
    private val rfqListingService: RfqListingService,
    private val matchingCreationService: MatchingCreationService,
) {
    @PostMapping("/rfqs")
    fun createRfq(
        @RequestBody createRfqRequest: CreateRfqRequest,
    ): ResponseEntity<String> {
        rfqCreationService.createRfq(createRfqRequest)
        return ResponseEntity.ok("RFQ has been created")
    }

    // todo: split to multiple controllers

    @PostMapping("/rfqs/{rfqId}/suppliers")
    fun addSuppliers(
        @RequestBody suppliers: AddSuppliersDto,
        @PathVariable rfqId: UUID,
    ): ResponseEntity<String> {
        // todo: add policy isAllowed (Policies.SuppliersAmount.isAllowed(..))
        if (suppliers.suppliers.size > 5) {
            // todo: do it better via controller advice
            return ResponseEntity.badRequest().build()
        }
        matchingCreationService.addSuppliers(rfqId, suppliers.suppliers)
        return ResponseEntity.ok("RFQ has been created")
    }

    @GetMapping("/rfqs/{rfqId}/suppliers")
    fun getMatches(
        @PathVariable rfqId: UUID,
    ): ResponseEntity<List<MatchedSupplierDto>> {
        return ResponseEntity.ok(matchingCreationService.getSuppliers(rfqId))
    }

    // todo: consider to have a pagination if too many
    @GetMapping("/rfqs")
    fun listAll(): ResponseEntity<List<ResponseRfqDto>> {
        return ResponseEntity.ok(rfqListingService.listAll())
    }
}

data class AddSuppliersDto(
    val suppliers: List<UUID>,
)

data class MatchedSupplierDto(
    val id: UUID,
    val name: String,
    val description: String,
    val status: String,
)